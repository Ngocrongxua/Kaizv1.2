package com.girlkun.models.map.gas;
//import com.girlkun.models.boss.bdkb.TrungUyXanhLo;

import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.list_boss.gas.DrLyChee;
import com.girlkun.models.boss.list_boss.gas.HaChiJack;
import com.girlkun.models.clan.Clan;
import com.girlkun.models.map.TrapMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author BTH public static final int MAX_AVAILABLE = 50;
 *
 *
 */
public class Gas {

    public static final long POWER_CAN_GO_TO_GAS = 40000000000L;
    public static final List<Gas> KHI_GAS;
    public static final int MAX_AVAILABLE = 500;
    public static final int TIME_KHI_GAS = 1800000;

    private Player player;

    static {
        KHI_GAS = new ArrayList<>();
        for (int i = 0; i < MAX_AVAILABLE; i++) {
            KHI_GAS.add(new Gas(i));
        }
    }

    public int id;
    public int level;
    public final List<Zone> zones;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;

    public Gas(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
    }

    public void update() {
        if (this.isOpened) {
            if (Util.canDoWithTime(lastTimeOpen, TIME_KHI_GAS)) {
                this.finish();
            }
        }
    }

    public void openGas(Player plOpen, Clan clan, int level) {
        this.level = level;
        this.lastTimeOpen = System.currentTimeMillis();
        this.isOpened = true;
        this.clan = clan;
        this.clan.timeOpenKhiGas =  System.currentTimeMillis();
        this.clan.playerOpenKhiGas = plOpen;
        this.clan.khiGas = this;
        resetGas();
        ChangeMapService.gI().goToGas(plOpen);
        sendTextGas();
    }
    public boolean isInitBoss;
    public void InitBoss(Player pl) 
    {
        try {
            new DrLyChee(pl, level, (int) 1, (int) 1, BossID.DR_LYCHEE);
            isInitBoss =true;
        } catch (Exception e)
        {
            System.err.print("\nError at 171\n");
            e.printStackTrace();
        }
    }

    private void resetGas() {
        for (Zone zone : zones) {
            for (Mob m : zone.mobs) 
            {
                Mob.initMopbKhiGas(m, this.level);
                Mob.hoiSinhMob(m);
            }
        }
    }

    //kết thúc bản đồ kho báu
    public void finish() {
        List<Player> plOutDT = new ArrayList();
        for (Zone zone : zones) {
            List<Player> players = zone.getPlayers();
            for (Player pl : players) {
                plOutDT.add(pl);
            }

        }
        for (Player pl : plOutDT) {
            ChangeMapService.gI().changeMapBySpaceShip(pl, 0, -1, 384);
        }
        this.clan.khiGas = null;
        this.clan = null;
        this.isOpened = false;
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    public static void addZone(int idGas, Zone zone) {
        KHI_GAS.get(idGas).zones.add(zone);
    }

    public void sendTextGas() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextGas(pl);
        }
    }
}
