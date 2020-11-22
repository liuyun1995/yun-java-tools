package com.liuyun.github;

import com.liuyun.github.utils.AlarmHelper;
import com.liuyun.github.utils.ErrorContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: lewis
 * @create: 2020/1/17 下午4:12
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BaseTest {

    @Test
    public void test() {
        AlarmHelper.pushMsg(ErrorContext.instance().message("丫丫丫丫"));
        System.out.println("成功");
    }

}
