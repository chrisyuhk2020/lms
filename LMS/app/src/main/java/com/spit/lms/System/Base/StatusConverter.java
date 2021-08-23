package com.spit.lms.System.Base;

public class StatusConverter {
    public static String getStatusById(int id) {
        if(id == 1) {
            return "未綁定";
        }
        if(id == 2) {
            return "在庫";
        }
        if(id == 3) {
            return "已借出";
        }
        if(id == 4) {
            return "預約中";
        }
        if(id == 5) {
            return "刪除";
        }
        if(id == 6) {
            return "丟失";
        }
        if(id == 7) {
            return "註銷";
        }
        if(id == 8) {
            return "註銷中";
        }
        if(id == 9) {
            return "異常";
        }
        if(id == 10) {
            return "不在庫";
        }
        if(id == 100) {
            return "抽樣";
        }
        if(id == 101) {
            return "全盤點";
        }
        if(id == 101) {
            return "已盤點";
        }
        if(id == 111) {
            return "未盤點";
        }
        if(id == 112) {
            return "盤點中";
        }
        if(id == 113) {
            return "盤點完成";
        }
        if(id == 114) {
            return "過期";
        }
        if(id == 115) {
            return "預備中";
        }
        if(id == 200) {
            return "禁用";
        }
        if(id == 201) {
            return "啟用";
        }
        if(id == 210) {
            return "已用";
        }
        if(id == 211) {
            return "未用";
        }
        if(id == 212) {
            return "損壞";
        }
        if(id == 213) {
            return "註銷";
        }
        if(id == 214) {
            return "丟失";
        }
        if(id == 215) {
            return "註銷中";
        }
        if(id == 216) {
            return "刪除";
        }
        if(id == 30) {
            return "借閱中";
        }
        if(id == 31) {
            return "已歸還";
        }
        if(id == 32) {
            return "逾期歸還";
        }
        if(id == 33) {
            return "已逾期";
        }
        return "";
    }
}
