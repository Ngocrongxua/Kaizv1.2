package com.girlkun.models.boss.dhvt;

import com.girlkun.consts.ConstRatio;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossStatus;
import static com.girlkun.models.boss.BossStatus.ACTIVE;
import static com.girlkun.models.boss.BossStatus.JOIN_MAP;
import static com.girlkun.models.boss.BossStatus.RESPAWN;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.PlayerService;
import com.girlkun.services.SkillService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;

/**
 * @author BTH sieu cap vippr0 
 */
public abstract class BossDHVT extends Boss {

    protected Player playerAtt;
    protected long timeJoinMap;

    public BossDHVT(byte id, BossData data) throws Exception {
        super(id, data);
        this.bossStatus = BossStatus.RESPAWN;
    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    protected void goToXY(int x, int y, boolean isTeleport) {
        if (!isTeleport) {
            byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
            byte move = (byte) Util.nextInt(50, 100);
            PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
        } else {
            ChangeMapService.gI().changeMapYardrat(this, this.zone, x, y);
        }
    }

    @Override
    public void attack() {
        try {
//            super.attack();
//            System.err.println("boss att");
            if (Util.canDoWithTime(timeJoinMap, 10000)) {
                if (playerAtt.location != null && playerAtt != null && playerAtt.zone != null && this.zone != null && this.zone.equals(playerAtt.zone)) {
                    if (this.isDie()) {
                        return;
                    }
                    this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                    if (Util.getDistance(this, playerAtt) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                            goToXY(playerAtt.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)), Util.nextInt(10) % 2 == 0 ? playerAtt.location.y : playerAtt.location.y - Util.nextInt(0, 50), false);
                        }
//                        System.err.println("attack player: " + playerAtt.name + "use skill: " + SkillService.gI().useSkill(this, playerAtt, null));
                        SkillService.gI().useSkill(this, playerAtt, null,null);
                        checkPlayerDie(playerAtt);
                    } else {
                        goToPlayer(playerAtt, false);
                    }
                } else {
                    this.leaveMap();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void goToPlayer(Player pl, boolean isTeleport) {
        goToXY(pl.location.x, pl.location.y, isTeleport);
    }

    @Override
    public void joinMap() {
        if (playerAtt.zone != null) {
            this.zone = playerAtt.zone;
            ChangeMapService.gI().changeMap(this, this.zone, 435, 264);
        }
    }

    protected void immortalMp() {
        this.nPoint.mp = this.nPoint.mpg;
    }

    @Override
    public void update() {
//        super.update();
        try {
            switch (this.bossStatus) {
                case RESPAWN:
                    this.respawn();
                    this.changeStatus(BossStatus.JOIN_MAP);
                case JOIN_MAP:
                    joinMap();
                    if (this.zone != null) {
                        changeStatus(BossStatus.ACTIVE);
                        timeJoinMap = System.currentTimeMillis();
                        this.immortalMp();
                        this.typePk = 3;
                    }
                    break;
                case ACTIVE:
//                        this.chatM();
                    if (this.playerSkill.prepareTuSat || this.playerSkill.prepareLaze || this.playerSkill.prepareQCKK) {
                        break;
                    } else {
                        this.attack();
                    }
                    break;
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.logException(Manager.class, e, "Lỗi update dhvt");
        }
    }

    protected void notifyPlayeKill(Player player) {

    }

    @Override
    public void die(Player plKill) {
//        if (plKill != null) {
//            reward(plKill);
//            ServerNotify.gI().notify(plKill.name + " vừa tiêu diệt được " + this.name + ", ghê chưa ghê chưa..");
//        }
        plKill.nPoint.hp=plKill.nPoint.hpMax;
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
