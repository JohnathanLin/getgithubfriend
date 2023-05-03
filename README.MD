# 批量读取github用户的azure functions
## 目的
在做博客友情链接时，我希望能够将朋友们的账号存储下来，然后从某个地方去拉取朋友们的昵称、头像、个性签名和个人网站地址。
我发现我身边做技术、玩博客的朋友们，大多都有github账号。

因此，我可以借助github提供的api，批量从github上获取朋友们的昵称、头像、个性签名和网站地址。

但是github访问是需要密钥的。而我的博客是hugo静态博客。直接从hugo访问github，显然不是一个安全的做法。

最后我选择使用Azure Functions的免费服务，免费100万次访问。通过云函数的方式帮我向github发送请求，获取朋友的信息。
## github graphQL请求
```
query {
  user1: user(login: "ABC") {
    ...UserFragment
  }
  user2: user(login: "DEF") {
    ...UserFragment
  }
  user3: user(login: "GHI") {
    ...UserFragment
  }
}

fragment UserFragment on User {
  login
  name
  avatarUrl
  bioHTML 
  websiteUrl  
}
```
请求明细如上，具体使用方式可以参考github graphQL的文档。


## local.settings.json
你需要在项目根目录新建一个local.settings.json文件，包含如下内容，然后修改GithubKeyForGetUser为你的key
```json
{
  "IsEncrypted": false,
  "Values": {
    "GithubKeyForGetUser": "你的github key（带有user权限）",
    "AzureWebJobsStorage": "removed",
    "FUNCTIONS_WORKER_RUNTIME": "java"
  }
}
```

## 部署
部署前需要申请storage accounts，然后找到Access keys的Connection string，填入app setting中。
