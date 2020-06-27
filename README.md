# AdvancedFilter
一个可自定义的过滤器

```YAML
keywords: #大小写不敏感
  stupideast: #规则名称，同时也是键值
    words: #过滤词列表
      - virus
      - ncov
    punish: command #执行类型 ： command-执行命令并取消事件（替换{player}{group}{keyword}） block-取消事件 replace-文字替换 silent-只向管理员发送警告
    extra: ban {player} Fuckyou #如果为command，则为命令；如果为replace，则为替换用的文本，如果替换的文本为空字符串，则替换为星号
  wow:
    words:
      - 傻逼
    punish: block
    extra: null
  nmsl:
    words:
      - nmsl
    punish: replace
    extra: wmsl
```
