package guru.springframework.brewery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@SpringBootTest : @SpringBootApplicationのあるメインクラスを見る、@ComponentScanの範囲のConfigクラスをcontextに登録。context全体を読み込むため遅い。
@RunWith(SpringRunner.class)
@SpringBootTest
public class TsbbSfgBreweryApplicationTests {

    @Test
    public void contextLoads() {
    }

}
