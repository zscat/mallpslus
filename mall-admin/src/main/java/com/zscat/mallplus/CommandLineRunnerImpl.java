package com.zscat.mallplus;

import com.zscat.mallplus.config.Constant;
import com.zscat.mallplus.sys.mapper.GeneratorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lnj
 * createTime 2018-11-07 22:25
 **/
@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    @Autowired
    GeneratorMapper generatorMapper;

    @Override
    public void run(String... args) throws Exception {
        List<Map<String, Object>> tables = generatorMapper.list();
        for (Map<String, Object> map : tables) {
            List<Map<String, String>> colus = generatorMapper.listColumns(map.get("tableName").toString());
            for (Map<String, String> mapc : colus) {
               if (mapc.get("columnName").equals("store_id")) {
                    Constant.Tables.put(map.get("tableName").toString(), "1");

                }
            }
        }


    }

    public static void main(String[] args) {
        System.out.println(5 % 2);
    }
}