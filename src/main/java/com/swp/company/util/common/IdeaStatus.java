package com.swp.company.util.common;


    public enum IdeaStatus {
        CHUA_PHAN_HOI("Chưa phản hồi"),
        DA_PHAN_HOI("Đã phản hồi");

        private final String text;

        IdeaStatus(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

