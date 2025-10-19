package com.gantenx.raffles.model.dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.gantenx.raffles.model.entity.Rule;
import com.gantenx.raffles.model.entity.RuleExample;
import com.gantenx.raffles.model.mapper.RuleMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class RuleDao {

    @Autowired
    private RuleMapper ruleMapper;

    public List<Rule> selectActiveRules() {
        RuleExample ruleExample = new RuleExample();
        ruleExample.createCriteria().andStatusEqualTo(1);
        return ruleMapper.selectByExample(ruleExample);
    }
}
