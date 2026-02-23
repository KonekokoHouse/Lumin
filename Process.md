完整移植SigmaJello的clickgui（除Music）到我这个项目来。
SigmaJello项目的 `c:\Users\L3MonKe\Desktop\Code\Lumin\src\main\resources\assets\lumin\jello` 相关图片资源已复制
请你使用当前项目的 `c:\Users\L3MonKe\Desktop\Code\Lumin\src\main\java\com\github\lumin\graphics` 绘制工具绘制
[请你务必仔细阅读所有graphics的使用方法，字体绘制就用现在的pingfang.ttf字体即可
资源获取用 `c:\Users\L3MonKe\Desktop\Code\Lumin\src\main\java\com\github\lumin\utils\resources\ResourceLocationUtils.java` 。
由于Minecraft api有许多变更，你需要阅读新版本 `c:\Users\L3MonKe\Desktop\Code\Lumin\致敬源码库\neoforge-21.11.38-beta-decompiled` Minecraft api源代码了解新版本变更，编写继承或需要Minecraft api的代码前必须仔细阅读源码。
ClickGui的基础架构我已经帮你写好了，剩下的你需要实现 `c:\Users\L3MonKe\Desktop\Code\Lumin\src\main\java\com\github\lumin\gui` 的视觉复现任务。
优先考虑质量而不是速度。

我为你制定了步骤，完成一步就在后面打钩，我们分步骤进行：
1. 先绘制出Category的Panel
2. 完成右键Module的控制面板，控制setting。
3. 实现裁剪和滚动效果。
4. 完整移植动画效果。
5. 完成最后的收尾工作，整体检查。