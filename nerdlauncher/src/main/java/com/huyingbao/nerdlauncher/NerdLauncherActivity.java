package com.huyingbao.nerdlauncher;

import android.support.v4.app.Fragment;

/**
 * 应用运行时，Android使用任务task来跟踪用户的状态。
 * 任务是一个activity栈。栈底部的activity通常称为基activity。
 * 栈顶的activity用户能看到。
 * <p>
 * 默认情况下，新activity都在当前任务task中启动。
 * 懂当前应用启动的任何activity都会添加到当前的activity栈中。
 * <p>
 * 对象需要内存和虚拟机的支持才能生存。
 * 进程是操作系统创建的、供应用对象生存以及应用运行的地方。
 * 进程通常会占用由操作系统管理的系统资源，如内存、网络端口以及打开的文件等。
 * 进程还拥有至少一个执行线程。
 * 在android系统中每个进程都需要一个虚拟机来运行。
 * <p>
 * Android世界里的每个应用组件都仅有一个进程相关联。
 * 应用伴随着自己的进程一起完成创建，该进程也是应用中所有组件的默认进程。
 * <p>
 * 虽然组件可以指派给不同的进程，但推荐使用默认进程。相比多进程的使用，多线程的使用更加方便。
 * <p>
 * 每一个activity实例都仅存在与一个进程process中，同一个任务task关联.
 * 任务task只包含activity，这些activity通常来自不同的应用进程；
 * 而进程包含了应用的全部运行代码和对象。
 * <p>
 * 从NerdLauncher启动器中启动CriminalIntent应用时，操作系统创建了一个CriminalIntent进程process
 * 以及一个以CrimeListActivity为基栈activity的新任务task
 *
 * 在Lollipop系统及以上版本中，对以android.intent.action.SEND或
 * action.intent.action.SEND_MULTIPLE启动的activity，
 * 隐式intent选择器会创建独立的新任务。
 * 在Lollipop中叫做并发文档（concurrent document）.
 * 有了并发文档，就可以为运行的应用动态创建任意数目的任务task。
 *
 * 在Lollipop设备上，如果需要应用启动多个任务：
 * 1：给intent打上Intent.FLAG_NEW_DOCUMENT标签flag；
 * 2：manifest文件中，为activity设置如下documentLaunchMode:
 * android:documentLaunchMode="intoExisting"
 */
public class NerdLauncherActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NerdLauncherFragment.newInstance();
    }
}
