//package net.jueb.util4j.hotSwap.springScript;
//
//import com.rgt.slg.gameserver.core.DBService;
//import com.rgt.slg.gameserver.core.SpringContext;
//import com.rgt.slg.gameserver.game.module.alliance.IAllianceBizService;
//import com.rgt.slg.gameserver.game.module.alliance.IAlliancePlayerService;
//import com.rgt.slg.gameserver.game.module.alliance.IAllianceService;
//import com.rgt.slg.gameserver.game.module.alliance.resource.IAllianceRssBizService;
//import com.rgt.slg.gameserver.game.module.gem.IGemBizService;
//import com.rgt.slg.gameserver.game.module.gridcache.impl.PlayerViewGridService;
//import com.rgt.slg.gameserver.game.module.init.IInitDataBizService;
//import com.rgt.slg.gameserver.game.module.player.IPlayerService;
//import com.rgt.slg.gameserver.id.ServerIdHolder;
//import com.rgt.slg.gameserver.script.GameScriptRunner;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.PostConstruct;
//import javax.script.Bindings;
//import javax.script.ScriptEngine;
//import javax.script.ScriptEngineManager;
//
//@Slf4j
//@Api(value = "脚本 接口")
//@RestController
//@RequestMapping("/script")
//public class ScriptController {
//
//
//    @ApiOperation("获取java脚本demo")
//    @GetMapping("/scriptDemo")
//    public String scriptDemo() throws Exception {
//        return "package com.rgt.slg.gameserver.http.script;\n" +
//                "\n" +
//                "import com.rgt.slg.gameserver.core.SpringContext;\n" +
//                "import com.rgt.slg.gameserver.script.GameScript;\n" +
//                "import org.springframework.context.ApplicationContext;\n" +
//                "\n" +
//                "public class GameScriptTask implements SpringBootScript {\n" +
//                "\n" +
//                "    final ApplicationContext ctx=SpringContext.getApplicationContext();\n" +
//                "\n" +
//                "    public String run() {\n" +
//                "        ScriptFunctionFacade sf=ctx.getBean(ScriptFunctionFacade.class);\n" +
//                "        //TODO START\n" +
//                "\n" +
//                "\n" +
//                "        //TODO END\n" +
//                "        return \"TestScript ok\";\n" +
//                "    }\n" +
//                "}";
//    }
//
//    @ApiOperation("执行java脚本")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "query", name = "className", dataType = "string", required = true, value = "完整类名",defaultValue = "com.rgt.slg.gameserver.http.script.GameScriptTask"),
//            @ApiImplicitParam(paramType = "body", name = "script", dataType = "string", required = true, value = "java脚本"
//                    )
//    })
//    @PostMapping("/execJava")
//    public String execJava(@RequestParam("className") String className, @RequestBody String script) throws Exception {
//        try {
//            String path=getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
//            SpringBootScriptRunner gameScriptRunner=new SpringBootScriptRunner(path);
//            return gameScriptRunner.runScript(className,script);
//        } catch (Throwable e) {
//            log.error(e.getMessage(),e);
//            return "# exception:" + e.getMessage();
//        }
//    }
//}