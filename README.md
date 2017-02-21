# data-processor

插件试文件处理器，可以定义插件类（实现LineDataProcessor），然后将插件打包，然后路径在本项目data-processor.xml中进行配置

参数：
1. 处理器方式，1、并行；2、链式处理
1. 线程数(若小于或等于0,则使用1线程)
1. 待处理目录或文件
1. 处理器class(可选，按逗号分隔)