package com.gantenx.raffles.model.mapper;

import com.gantenx.raffles.model.entity.Rule;
import com.gantenx.raffles.model.entity.RuleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RuleMapper {
    long countByExample(RuleExample example);

    int deleteByExample(RuleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Rule row);

    int insertSelective(Rule row);

    List<Rule> selectByExample(RuleExample example);

    Rule selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") Rule row, @Param("example") RuleExample example);

    int updateByExample(@Param("row") Rule row, @Param("example") RuleExample example);

    int updateByPrimaryKeySelective(Rule row);

    int updateByPrimaryKey(Rule row);
}