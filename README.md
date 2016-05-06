# jsonParserWithGroovy

1. 用java和groovy写得json解析库，能够完成字符串、json对象（数组）、java对象、集合之间的转换.

2. 实现了进一步拓展，实现了json格式内容和xml格式内容的相互转换，还拓展了格式化并着色显示json字2符串、转义、反转义、语法校验等功能。简单用servlet设计了一个web界面，用于实现用户功能交互。

3. 项目先用java实现了大部分，后全部内容用动态脚本语法groovy重新实现一遍，由于groovy的动态语言特性，在进行json对象与pojo类对象的转换存在一定的bug。

4. 程序使用了大量的递归算法，有一定的参考和研读价值