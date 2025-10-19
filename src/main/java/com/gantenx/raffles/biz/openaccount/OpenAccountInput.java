package com.gantenx.raffles.biz.openaccount;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAccountInput {

    private static final long serialVersionUID = -1153906651043317309L;

    private String ib_account;

    private String customer_id;

    private String background_check_comment;

    private Integer face2face_review;

    private Integer third_party_cdd;

    private String legal_residence_country;

    private String margin;

    private String bs_type;

    private Date birthday;

    private String job;

    private String career;

    private String career_other;

    private String business;

    private String business_other;

    private String issue_country;

    private String net_assets;

    private Double net_assets_avg;

    private String net_year_income;

    private Double net_year_income_avg;

    private Integer account_count;

    private Boolean isReopen;

    private String country;

    private String open_account_purpose;

    private String customer_type;

    private String additional_source_income;

    private String wealth_source;

    private String product_service;

    private String expected_deposit_amount;
}
