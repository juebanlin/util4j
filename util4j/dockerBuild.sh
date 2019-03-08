#!/usr/bin/env bash

#仓库用户
registryUser=admin
#仓库密码
registryPwd=xxxxxx
#仓库地址
registry=cloud.jueb.net:50007
#本地镜像名字标签
localNameTag=demo/spring-boot-demo
#远程镜像名字标签
remoteNameTag=demo/spring-boot-demo:last

#mvn打包
mvn clean package -Dmaven.test.skip=true -U

#以当前目录dockerfile构建一个demo/springBootDemo标签的镜像
docker build -t $localNameTag .

#在项目中标记镜像:
docker tag $localNameTag $registry/$remoteNameTag

#登录到指定仓库
docker login -u $registryUser -p $registryPwd $registry

#推送镜像到当前项目:
docker push $registry/$remoteNameTag

#登出仓库
docker logout $registry

read -p "Press any key to continue"