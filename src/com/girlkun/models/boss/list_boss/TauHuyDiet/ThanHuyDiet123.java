package com.girlkun.models.boss.list_boss.TauHuyDiet;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.Random;


public class ThanHuyDiet123 extends Boss {
    private long lasttimehakai;
    private int timehakai;

    public ThanHuyDiet123() throws Exception {
        super(Util.randomBossId(), BossesData.THAN_HUY_DIET1);
    }

    @Override
   public void reward(Player plKill) {
        rewardFutureBoss(plKill);
    }

    @Override
    public int injured(Player plAtt, int damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage/2;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
    private long st;


    @Override
    public void active() {

        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.huydiet();
        this.attack();
    }

    private void huydiet() {
        if (!Util.canDoWithTime(this.lasttimehakai, this.timehakai) || !Util.isTrue(1, 100)) {
            return;
        }

        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null || pl.isDie()) {
            return;
        }
        this.nPoint.dameg += (pl.nPoint.dame * 5 / 100);
        this.nPoint.hpg += (pl.nPoint.hp * 2 / 100);
        this.nPoint.critg++;
        this.nPoint.calPoint();
        PlayerService.gI().hoiPhuc(this, pl.nPoint.hp, 0);
        pl.injured(null, pl.nPoint.hpMax, true, false);
        Service.gI().sendThongBao(pl, "Bạn vừa bị " + this.name + " cho bay màu");
        this.chat(2, "Hắn ta mạnh quá,coi chừng " + pl.name + ",tên " + this.name + " hắn không giống như những kẻ thù trước đây");
        this.chat("Thật là yếu ớt " + pl.name);
        this.lasttimehakai = System.currentTimeMillis();
        this.timehakai = Util.nextInt(20000, 30000);
    }
}


