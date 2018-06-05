package com.actions.model;

import java.util.List;

/**
 * Created by yison on 2018/5/28.
 */

public class CheckUpdateType {

    /**
     * list : [{"configValue":"NK","id":3,"typeId":2,"typeValue":"TEST"}]
     * state : 200
     * msg : OK
     */

    private int state;
    private String msg;
    private List<ListBean> list;

    @Override
    public String toString() {
        return "CheckUpdateType{" +
                "state=" + state +
                ", msg='" + msg + '\'' +
                ", list=" + list +
                '}';
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * configValue : NK
         * id : 3
         * typeId : 2
         * typeValue : TEST
         */

        private String configValue;
        private int id;
        private int typeId;
        private String typeValue;

        public String getConfigValue() {
            return configValue;
        }

        public void setConfigValue(String configValue) {
            this.configValue = configValue;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public String getTypeValue() {
            return typeValue;
        }

        public void setTypeValue(String typeValue) {
            this.typeValue = typeValue;
        }
    }
}
