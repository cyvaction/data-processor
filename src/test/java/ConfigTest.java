import com.yulore.bigdata.data_processor.common.config.ProcessorConfig;
import com.yulore.bigdata.data_processor.common.constant.ContextConstant;


public class ConfigTest {
	public static void main(String[] args) {
		ProcessorConfig config = new ProcessorConfig(ContextConstant.PROCESSOR_CONFIG_PATH);
		System.out.println(config.isRemoveRepeat());
	}
}
