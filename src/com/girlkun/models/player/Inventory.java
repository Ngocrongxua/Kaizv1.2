package com.girlkun.models.player;

import java.util.ArrayList;
import java.util.List;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.services.GiftService;


public class Inventory {

    public static final long LIMIT_GOLD = 200000000000L;
  //  public static final int LIMIT_GOLD = 200000000000;

    public static final int MAX_ITEMS_BAG = 80;
    public static final int MAX_ITEMS_BOX = 80;


    public Item trainArmor;
    public List<String> giftCode;
    public List<Item> itemsBody;
    public List<Item> itemsBag;
    public List<Item> itemsBox;

    public List<Item> itemsBoxCrackBall;

    public long gold;
    public int gem;
    public int ruby;
    public int tien;
    public int coupon;
    public int event;

    public List<Item> itemsMailBox;
    public Inventory() {
        itemsBody = new ArrayList<>();
        itemsBag = new ArrayList<>();
        itemsBox = new ArrayList<>();
        itemsBoxCrackBall = new ArrayList<>();
        itemsMailBox = new ArrayList<>();
        giftCode = new ArrayList<>();
    }

    public int getGemAndRubyAndTien() {
        return this.gem + this.ruby + this.tien;
    }
    
    
    public int getParam(Item it , int id){
        for(ItemOption op : it.itemOptions){
            if(op!=null&&op.optionTemplate.id ==id){
                return op.param;
            }
        }
        return 0;
    }
    
    public boolean haveOption(List<Item> l , int index , int id){
        Item it = l.get(index);
        if(it != null && it.isNotNullItem()){
            return it.itemOptions.stream().anyMatch(op -> op != null && op.optionTemplate.id == id);
        }
        return false;
    }

    public void subGemAndRubyAndTien(int num) {
        this.ruby -= num;
        if (this.ruby < 0) {
            this.gem += this.ruby += this.tien;
            this.ruby = 0;
        }
    }

    public void addGold(int gold) {
        this.gold += gold;
        if (this.gold > LIMIT_GOLD) {
            this.gold = LIMIT_GOLD;
        }
    }

    public void dispose() {
        if (this.trainArmor != null) {
            this.trainArmor.dispose();
        }
        this.trainArmor = null;
        if(this.itemsBody!= null){
            for(Item it : this.itemsBody){
                it.dispose();
            }
            this.itemsBody.clear();
        }
        if(this.itemsBag!= null){
            for(Item it : this.itemsBag){
                it.dispose();
            }
            this.itemsBag.clear();
        }
        if(this.itemsBox!= null){
            for(Item it : this.itemsBox){
                it.dispose();
            }
            this.itemsBox.clear();
        }
        if(this.itemsBoxCrackBall!= null){
            for(Item it : this.itemsBoxCrackBall){
                it.dispose();
            }
            this.itemsBoxCrackBall.clear();
        }
        this.itemsBody = null;
        this.itemsBag = null;
        this.itemsBox = null;
        this.itemsBoxCrackBall = null;
    }


}
