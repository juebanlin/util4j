#指定基础镜像
FROM hub.c.163.com/library/java:8-alpine

#指定容器启动时执行命令的目录
WORKDIR /

#导出端口
EXPOSE 8080

#ADD命令:将主机构建环境（上下文）目录中的文件和目录、以及一个URL标记的文件 拷贝到镜像中
#如果源文件是个归档文件（压缩文件），则docker会自动帮解压。
#COPY命令:COPY指令和ADD指令功能和使用方式类似。只是COPY指令不会做自动解压工作:ADD zip.tar /myzip
ADD target/*.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
