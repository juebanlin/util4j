package net.jueb.util4j.kotlin.util

import kotlinx.coroutines.*
import java.lang.Runnable

fun execInCustomerThread(tasks: Collection<Runnable>,dispatcher: CoroutineDispatcher) = runBlocking {
    tasks.forEach {
        launch(context = dispatcher) {
            it.run()
        }
    }
}
fun execInCustomerThread(vararg tasks: Runnable,dispatcher: CoroutineDispatcher) = runBlocking {
    tasks.forEach {
        launch(context = dispatcher) {
            it.run()
        }
    }
}

//Dispatchers.Main	主线程，和UI交互，执行轻量任务	1.call suspend functions。2. call UI functions。 3. Update LiveData
fun execInMainThread(tasks: Collection<Runnable>) = runBlocking {
    tasks.forEach {
        launch(context = Dispatchers.Main) {
            it.run()
        }
    }
}
//Dispatchers.Main	主线程，和UI交互，执行轻量任务	1.call suspend functions。2. call UI functions。 3. Update LiveData
fun execInMainThread(vararg tasks: Runnable) = runBlocking {
    tasks.forEach {
        launch(context = Dispatchers.Main) {
            it.run()
        }
    }
}

//Dispatchers.IO	用于网络请求和文件访问	1. Database。 2.Reading/writing files。3. Networking
fun execInIoThread(tasks: Collection<Runnable>) = runBlocking {
    tasks.forEach {
        launch(context = Dispatchers.IO) {
            it.run()
        }
    }
}
//Dispatchers.IO	用于网络请求和文件访问	1. Database。 2.Reading/writing files。3. Networking
fun execInIoThread(vararg tasks: Runnable) = runBlocking {
    tasks.forEach {
        launch(context = Dispatchers.IO) {
            it.run()
        }
    }
}

//Dispatchers.Default	CPU密集型任务	1. Sorting a list。 2.Parsing JSON。 3.DiffUtils
fun execInCalcThread(tasks: Collection<Runnable>) = runBlocking {
    tasks.forEach {
        launch(context = Dispatchers.Default) {
            it.run()
        }
    }
}

fun execInCalcThreadV2(tasks: Collection<Runnable>) = runBlocking {
    tasks.forEach {
        async(context = Dispatchers.Default) {
            it.run()
        }
    }
}

//Dispatchers.Default	CPU密集型任务	1. Sorting a list。 2.Parsing JSON。 3.DiffUtils
fun execInCalcThread(vararg tasks: Runnable) = runBlocking {
    tasks.forEach {
        launch(context = Dispatchers.Default) {
            it.run()
        }
    }
}

//Dispatchers.Unconfined	不限制任何制定线程	高级调度器，不应该在常规代码里使用
fun execInCurrThread(tasks: Collection<Runnable>) = runBlocking {
    tasks.forEach {
        launch(context = Dispatchers.Unconfined) {
            it.run()
        }
    }
}
//Dispatchers.Unconfined	不限制任何制定线程	高级调度器，不应该在常规代码里使用
fun execInCurrThread(vararg tasks: Runnable) = runBlocking {
    tasks.forEach {
        launch(context = Dispatchers.Unconfined) {
            it.run()
        }
    }
}







