package com.girlkun.models.map;

import com.girlkun.consts.ConstTask;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.dhvt.BossDHVT;
import com.girlkun.models.item.Item;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.npc.NpcManager;
import com.girlkun.models.player.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.Referee;
import com.girlkun.models.player.TestDame;
import com.girlkun.network.io.Message;
import com.girlkun.services.ItemMapService;
import com.girlkun.services.ItemService;
import com.girlkun.services.MapService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.services.InventoryServiceNew;
//import com.girlkun.services.NgocRongNamecService;
import com.girlkun.utils.FileIO;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Zone {

    public static final byte PLAYERS_TIEU_CHUAN_TRONG_MAP = 12;

    public int countItemAppeaerd = 0;

    public Map map;
    public int zoneId;
    public int maxPlayer;
    public int hours;

    private final List<Player> humanoids; // player, boss, pet
    private final List<Player> notBosses; // player, pet
    private final List<Player> players; // player
    public final List<Player> bosses; // boss
    private final List<Player> pets; // pet

    public final List<Mob> mobs;
    public final List<ItemMap> items;

    public long lastTimeDropBlackBall;
    public boolean finishBlackBallWar;
    public boolean finishMapMaBu;

    public List<TrapMap> trapMaps;
    public boolean finishnguhs;
    @Setter
    @Getter
    private Player referee;

    public boolean isFullPlayer() {
        return this.players.size() >= this.maxPlayer;
    }

    public boolean isTrungUyTrangAlive;
    public boolean isbulon13Alive;
    public boolean isbulon14Alive;

    private void udMob() {
        for (Mob mob : this.mobs) {
            mob.update();
        }
    }


    private void udItem() {
        for (int i = this.items.size() - 1; i >= 0; i--) {
            this.items.get(i).update();
        }
    }

    public void update() {
        udMob();
        udPlayer();
        udItem();
    }
    

    public Zone(Map map, int zoneId, int maxPlayer) {
        this.map = map;
        this.zoneId = zoneId;
        this.maxPlayer = maxPlayer;
        this.humanoids = new ArrayList<>();
        this.notBosses = new ArrayList<>();
        this.players = new ArrayList<>();
        this.bosses = new ArrayList<>();
        this.pets = new ArrayList<>();
        this.mobs = new ArrayList<>();
        this.items = new ArrayList<>();
        this.trapMaps = new ArrayList<>();
    }

    public int getNumOfPlayers() {
        return this.players.size();
    }

    public boolean isBossCanJoin(Boss boss) {
        for (Player b : this.bosses) {
            if (b.id == boss.id) {
                return false;
            }
        }
        return true;
    }
    
    private void udPlayer() {
        for (int i = this.notBosses.size() - 1; i >= 0; i--) {
            if (i >= 0 && i < this.notBosses.size()) { // Check if the index is valid
                Player pl = this.notBosses.get(i);
                if (!pl.isBoss && !pl.isNewPet) {
                    pl.update();
                }
            }
        }
    }

    public boolean IsTrongTaiDeoCoTrongKhu() {
        for (Player b : this.players) {
            if (b.id == -1000000) {
                return false;
            }
        }
        return true;
    }

    public List<Player> getNotBosses() {
        return this.notBosses;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public List<Player> getHumanoids() {
        return this.humanoids;
    }

    public List<Player> getBosses() {
        return this.bosses;
    }

    public void addPlayer(Player player) {
        if (player != null) {
            if (!this.humanoids.contains(player)) {
                this.humanoids.add(player);
            }
            if (!player.isBoss && !this.notBosses.contains(player)) {
                this.notBosses.add(player);
            }
            if (!player.isBoss && !player.isNewPet && !player.isPet && !this.players.contains(player)) {
                this.players.add(player);
            }
            if (player.isBoss) {
                this.bosses.add(player);
            }
            if (player.isPet || player.isNewPet) {
                this.pets.add(player);
            }
        }
    }

    public boolean isKhongCoTrongTaiTrongKhu() {
        boolean cccccc = true;
        for (Player pl : players) {
            if (pl.name.compareTo("Trọng Tài") == 0) {
                cccccc = false;
                break;
            }
            if (pl.zone.map.mapId >= 21 && pl.zone.map.mapId <= 23) {
                cccccc = false;
            }
        }
        return cccccc;
    }

    public void removePlayer(Player player) {
        this.humanoids.remove(player);
        this.notBosses.remove(player);
        this.players.remove(player);
        this.bosses.remove(player);
        this.pets.remove(player);
    }

    public ItemMap getItemMapByItemMapId(int itemId) {
        for (ItemMap item : this.items) {
            if (item.itemMapId == itemId) {
                return item;
            }
        }
        return null;
    }

    public ItemMap getItemMapByTempId(int tempId) {
        for (ItemMap item : this.items) {
            if (item.itemTemplate.id == tempId) {
                return item;
            }
        }
        return null;
    }

    public List<ItemMap> getItemMapsForPlayer(Player player) {
        List<ItemMap> list = new ArrayList<>();
        for (ItemMap item : items) {
            if (item.itemTemplate.id == 78) {
                if (TaskService.gI().getIdTask(player) != ConstTask.TASK_3_1) {
                    continue;
                }
            }
            if (item.itemTemplate.id == 74) {
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_3_0) {
                    continue;
                }
            }
            list.add(item);
        }
        return list;
    }

    public Player getPlayerInMap(long idPlayer) {
        for (Player pl : humanoids) {
            if (pl.id == idPlayer) {
                return pl;
            }
        }
        return null;
    }

    public void pickItem(Player player, int itemMapId) {
        ItemMap itemMap = getItemMapByItemMapId(itemMapId);
        if (itemMap != null) {
            if (itemMap.playerId == player.id || itemMap.playerId == -1) {
                Item item = ItemService.gI().createItemFromItemMap(itemMap);
                boolean picked = true;
                if (!ItemMapService.gI().isNamecBall(item.template.id)) {
                    picked = InventoryServiceNew.gI().addItemBag(player, item);
                }
                if (picked) {
                    int itemType = item.template.type;
                    Message msg;
                    try {
                        msg = new Message(-20);
                        msg.writer().writeShort(itemMapId);
                        switch (itemType) {
                            case 9:
                            case 10:
                            case 34:
                                msg.writer().writeUTF("");
                                PlayerService.gI().sendInfoHpMpMoney(player);
                                break;
                            default:
                                switch (item.template.id) {
                                    case 362:
                                        Service.gI().sendThongBao(player, "Chỉ là cục đá thôi, nhặt làm gì?");
                                        break;
                                    case 353:
                                    case 354:
                                    case 355:
                                    case 356:
                                    case 357:
                                    case 358:
//                                    case 359:
//                                        if (System.currentTimeMillis() >= NgocRongNamecService.gI().tOpenNrNamec) {
//                                            if (player.idNRNM == -1) {
//                                                PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.PK_ALL);
//                                                player.idNRNM = item.template.id;
//                                                NgocRongNamecService.gI().mapNrNamec[item.template.id
//                                                        - 353] = player.zone.map.mapId;
//                                                NgocRongNamecService.gI().nameNrNamec[item.template.id
//                                                        - 353] = player.zone.map.mapName;
//                                                NgocRongNamecService.gI().zoneNrNamec[item.template.id
//                                                        - 353] = (byte) player.zone.zoneId;
//                                                NgocRongNamecService.gI().pNrNamec[item.template.id
//                                                        - 353] = player.name;
//                                                NgocRongNamecService.gI().idpNrNamec[item.template.id
//                                                        - 353] = (int) player.id;
//                                                player.lastTimePickNRNM = System.currentTimeMillis();
//                                                Service.gI().sendFlagBag(player);
//                                                msg.writer().writeUTF("Bạn đã nhặt được " + item.template.name);
//                                                msg.writer().writeShort(item.quantity);
//                                                player.sendMessage(msg);
//                                                msg.cleanup();
//                                            } else {
//                                                Service.gI().sendThongBao(player, "Bạn đã mang ngọc rồng trên người");
//                                            }
//                                        } else {
//                                            Service.gI().sendThongBao(player, "Chỉ là cục đá thôi, nhặt làm gì?");
//                                        }
//                                        break;
                                    case 73:
                                        msg.writer().writeUTF("");
                                        msg.writer().writeShort(item.quantity);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        break;
                                    case 74:
                                        msg.writer().writeUTF("Bạn mới vừa ăn " + item.template.name);
                                        break;
                                    case 78:
                                        msg.writer().writeUTF("Wow, một cậu bé dễ thương!");
                                        msg.writer().writeShort(item.quantity);
                                        player.sendMessage(msg);
                                        msg.cleanup();
                                        break;
                                    default:
                                        if (item.template.type >= 0 && item.template.type < 5) {
                                            msg.writer().writeUTF(item.template.name + " ngon ngon...");
                                        } else {
                                            msg.writer().writeUTF("Bạn mới nhặt được " + item.template.name);
                                        }
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        break;
                                }

                        }
                        msg.writer().writeShort(item.quantity);
                        player.sendMessage(msg);
                        msg.cleanup();
                        Service.gI().sendToAntherMePickItem(player, itemMapId);
                        if (!(this.map.mapId >= 21 && this.map.mapId <= 23
                                && itemMap.itemTemplate.id == 74
                                || this.map.mapId >= 42 && this.map.mapId <= 44
                                && itemMap.itemTemplate.id == 78)) {
                            removeItemMap(itemMap);
                        }
                    } catch (Exception e) {
                        Logger.logException(Zone.class, e);
                    }
                } else {
                    if (!ItemMapService.gI().isBlackBall(item.template.id)) {
                        String text = "Hành trang không còn chỗ trống";
                        Service.gI().sendThongBao(player, text);
                    }
                }
//                 if (!picked) {
//                 ItemMap itm = new ItemMap(itemMap);
//                 itm.x = player.location.x + Util.nextInt(-20, 20);
//                 itm.y = itm.zone.map.yPhysicInTop(itm.x, player.location.y);
//                 Service.gI().dropItemMap(player.zone, itm);
//                 }
            } else {
                Service.gI().sendThongBao(player, "Không thể nhặt vật phẩm của người khác");
            }
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
        TaskService.gI().checkDoneTaskPickItem(player, itemMap);
        TaskService.gI().checkDoneSideTaskPickItem(player, itemMap);
    }

    public void addItem(ItemMap itemMap) {
        if (itemMap != null && !items.contains(itemMap)) {
            items.add(0, itemMap);
        }
    }

    public void removeItemMap(ItemMap itemMap) {
        this.items.remove(itemMap);
    }

    public Player getRandomPlayerInMap() {
        if (!this.notBosses.isEmpty()) {
            return this.notBosses.get(Util.nextInt(0, this.notBosses.size() - 1));
        } else {
            return null;
        }
    }

    public void load_Me_To_Another(Player player) { // load thông tin người chơi cho những người chơi khác
        try {
            if (player.zone != null) {
                if (MapService.gI().isMapOffline(this.map.mapId)) {
                    if (player.isPet && this.equals(((Pet) player).master.zone)) {
                        infoPlayer(((Pet) player).master, player);
                    }
                } else {
                    for (int i = 0; i < players.size(); i++) {
                        Player pl = players.get(i);
                        if (!player.equals(pl)) {
                            infoPlayer(pl, player);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }

    public void load_Another_To_Me(Player player) { // load những player trong map và gửi cho player vào map
        try {
            if (MapService.gI().isMapOffline(this.map.mapId)) {
                for (Player pl : this.humanoids) {
                    if (pl.id == -player.id) {
                        infoPlayer(player, pl);
                        break;
                    }
                }
            } else {
                for (Player pl : this.humanoids) {
                    if (pl != null && !player.equals(pl)) {
                        infoPlayer(player, pl);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }

    public void loadBoss(Boss boss) {
        try {
            if (MapService.gI().isMapOffline(this.map.mapId)) {
                for (Player pl : this.humanoids) {
                    if (pl.id == -boss.id) {
                        infoPlayer(boss, pl);
                        break;
                    }
                }
            } else {
                for (Player pl : this.bosses) {
                    if (!boss.equals(pl)) {
                        infoPlayer(boss, pl);
                        infoPlayer(pl, boss);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }

    private void infoPlayer(Player plReceive, Player plInfo) {
        Message msg;
        try {
            String name;
            msg = new Message(-5);
            msg.writer().writeInt((int) plInfo.id);
            if (plInfo.clan != null) {
                msg.writer().writeInt(plInfo.clan.id);
                name = "[" + plInfo.clan.name + "]" + plInfo.name;
            } else {
                msg.writer().writeInt(-1);
                name = plInfo.name;
            }
            msg.writer().writeByte(Service.gI().getCurrLevel(plInfo));
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(plInfo.typePk);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeShort(plInfo.getHead());
            msg.writer().writeUTF(name);
            msg.writer().writeInt(plInfo.nPoint.hp);
            msg.writer().writeInt(plInfo.nPoint.hpMax);
            msg.writer().writeShort(plInfo.getBody());
            msg.writer().writeShort(plInfo.getLeg());
            msg.writer().writeByte(plInfo.getFlagBag()); // bag
            msg.writer().writeByte(-1);
            msg.writer().writeShort(plInfo.location.x);
            msg.writer().writeShort(plInfo.location.y);
            msg.writer().writeShort(0);
            msg.writer().writeShort(0); //

            msg.writer().writeByte(0);

            msg.writer().writeByte(plInfo.iDMark.getIdSpaceShip());

            msg.writer().writeByte(plInfo.effectSkill.isMonkey ? 1 : 0);
            msg.writer().writeShort(plInfo.getMount());
            msg.writer().writeByte(plInfo.cFlag);
            msg.writer().writeByte(0);

            // if (plInfo.isPl()) {
            msg.writer().writeShort(plInfo.isPl() ? plInfo.idAura : -1); // idauraeff
            msg.writer().writeByte(-1); // idauraeff
            msg.writer().writeShort(-1); // seteff
            // }

            plReceive.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            // Logger.logException(MapService.class, e);
        }
        Service.gI().sendFlagPlayerToMe(plReceive, plInfo);
        if (!plInfo.isBoss && !plInfo.isPet &&!plInfo.isNewPet && !(plInfo instanceof BossDHVT) && !(plInfo instanceof Referee&& ! (plInfo instanceof TestDame))) {
            Service.gI().sendPetFollowToMe(plReceive, plInfo);
            if (plInfo.inventory.itemsBody.get(12).isNotNullItem()) {
                Service.getInstance().sendFootRv(plInfo, plReceive, (short) plInfo.inventory.itemsBody.get(12).template.id);
            }
            if (plInfo.inventory.itemsBody.get(11).isNotNullItem()) {
                Service.gI().sendTitleRv1(plInfo, plReceive, (short) (plInfo.inventory.itemsBody.get(11).template.id));
            }

        }

        try {
            if (plInfo.isDie()) {
                msg = new Message(-8);
                msg.writer().writeInt((int) plInfo.id);
                msg.writer().writeByte(0);
                msg.writer().writeShort(plInfo.location.x);
                msg.writer().writeShort(plInfo.location.y);
                plReceive.sendMessage(msg);
                msg.cleanup();
            }
        } catch (Exception e) {

        }
    }

    public void mapInfo(Player pl) {
        Message msg;
        try {
            msg = new Message(-24);
            msg.writer().writeByte(this.map.mapId);
            msg.writer().writeByte(this.map.planetId);
            msg.writer().writeByte(this.map.tileId);
            msg.writer().writeByte(this.map.bgId);
            msg.writer().writeByte(this.map.type);
            msg.writer().writeUTF(this.map.mapName);
            msg.writer().writeByte(this.zoneId);

            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);

            // waypoint
            List<WayPoint> wayPoints = this.map.wayPoints;
            msg.writer().writeByte(wayPoints.size());
            for (WayPoint wp : wayPoints) {
                msg.writer().writeShort(wp.minX);
                msg.writer().writeShort(wp.minY);
                msg.writer().writeShort(wp.maxX);
                msg.writer().writeShort(wp.maxY);
                msg.writer().writeBoolean(wp.isEnter);
                msg.writer().writeBoolean(wp.isOffline);
                msg.writer().writeUTF(wp.name);
            }
            // mob
            List<Mob> mobs = this.mobs;
            msg.writer().writeByte(mobs.size());
            for (Mob mob : mobs) {
                msg.writer().writeBoolean(false); // is disable
                msg.writer().writeBoolean(false); // is dont move
                msg.writer().writeBoolean(false); // is fire
                msg.writer().writeBoolean(false); // is ice
                msg.writer().writeBoolean(false); // is wind
                msg.writer().writeByte(mob.tempId);
                msg.writer().writeByte(0);
                msg.writer().writeInt(mob.point.gethp());
                msg.writer().writeByte(mob.level);
                msg.writer().writeInt((mob.point.getHpFull()));
                msg.writer().writeShort(mob.location.x);
                msg.writer().writeShort(mob.location.y);
                msg.writer().writeByte(mob.status);
                msg.writer().writeByte(mob.lvMob);
                msg.writer().writeBoolean(false);
            }

            msg.writer().writeByte(0);

            // npc
            List<Npc> npcs = NpcManager.getNpcsByMapPlayer(pl);
            msg.writer().writeByte(npcs.size());
            for (Npc npc : npcs) {
                msg.writer().writeByte(npc.status);
                msg.writer().writeShort(npc.cx);
                msg.writer().writeShort(npc.cy);
                msg.writer().writeByte(npc.tempId);
                msg.writer().writeShort(npc.avartar);
            }

            // item
            List<ItemMap> itemsMap = this.getItemMapsForPlayer(pl);
            msg.writer().writeByte(itemsMap.size());
            for (ItemMap it : itemsMap) {
                msg.writer().writeShort(it.itemMapId);
                msg.writer().writeShort(it.itemTemplate.id);
                msg.writer().writeShort(it.x);
                msg.writer().writeShort(it.y);
                msg.writer().writeInt((int) it.playerId);
            }

            // bg item
            // msg.writer().writeShort(0);
            try {
                byte[] bgItem = FileIO.readFile("data/girlkun/map/item_bg_map_data/" + this.map.mapId);
                msg.writer().write(bgItem);
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }

            // eff item
            // msg.writer().writeShort(0);
            try {
                byte[] effItem = FileIO.readFile("data/girlkun/map/eff_map/" + this.map.mapId);
                msg.writer().write(effItem);
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }

            msg.writer().writeByte(this.map.bgType);
            msg.writer().writeByte(pl.iDMark.getIdSpaceShip());
            msg.writer().writeByte(this.map.mapId == 149 ? 1 : 0);
//            msg.writer().writeByte(this.map.mapId == 149 ? 1 : 0);
            pl.sendMessage(msg);

            msg.cleanup();

        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public TrapMap isInTrap(Player player) {
        for (TrapMap trap : this.trapMaps) {
            if (player.location.x >= trap.x && player.location.x <= trap.x + trap.w
                    && player.location.y >= trap.y && player.location.y <= trap.y + trap.h) {
                return trap;
            }
        }
        return null;
    }

    public int getNumOfBosses() {
        return this.bosses.size();
    }
}
