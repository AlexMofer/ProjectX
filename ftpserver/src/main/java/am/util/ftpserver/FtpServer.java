/*
 * Copyright (C) 2019 AlexMofer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.util.ftpserver;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.ftpserver.ConnectionConfig;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.message.MessageResource;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * FTP服务器
 * Created by Alex on 2019/10/7.
 */
@SuppressWarnings("unused")
public class FtpServer {

    private final org.apache.ftpserver.FtpServer mServer;

    private FtpServer(org.apache.ftpserver.FtpServer server) {
        mServer = server;
    }

    /**
     * 开启服务
     *
     * @throws Exception 错误
     */
    public void start() throws Exception {
        mServer.start();
    }

    /**
     * 停止服务
     */
    public void stop() {
        mServer.stop();
    }

    /**
     * 判断服务是否停止
     *
     * @return 服务停止时返回true
     */
    public boolean isStopped() {
        return mServer.isStopped();
    }

    /**
     * 暂停服务（对阵在执行的操作无效）
     */
    public void suspend() {
        mServer.suspend();
    }

    /**
     * 判断服务是否已经暂停
     *
     * @return 服务暂停时返回true
     */
    public boolean isSuspended() {
        return mServer.isSuspended();
    }

    /**
     * 恢复服务
     */
    public void resume() {
        mServer.resume();
    }

    /**
     * 创建FTP服务器
     *
     * @param listeners         服务器监听
     * @param ftplets           服务器程序
     * @param userManager       用户管理器
     * @param fileSystemFactory 文件系统工厂
     * @param commandFactory    命令工厂
     * @param messageResource   消息资源
     * @param connectionConfig  连接配置
     * @return FTP服务器
     */
    public static FtpServer createServer(final Map<String, Listener> listeners,
                                         final Map<String, Ftplet> ftplets,
                                         final UserManager userManager,
                                         final FileSystemFactory fileSystemFactory,
                                         final CommandFactory commandFactory,
                                         final MessageResource messageResource,
                                         final ConnectionConfig connectionConfig) {
        final FtpServerFactory factory = new FtpServerFactory();
        factory.setListeners(listeners);
        if (ftplets != null && !ftplets.isEmpty())
            factory.setFtplets(ftplets);
        factory.setUserManager(userManager);
        factory.setFileSystem(fileSystemFactory);
        if (commandFactory != null)
            factory.setCommandFactory(commandFactory);
        if (messageResource != null)
            factory.setMessageResource(messageResource);
        factory.setConnectionConfig(connectionConfig);
        return new FtpServer(factory.createServer());
    }

    /**
     * 创建FTP服务器
     *
     * @param port      端口
     * @param adminName 管理员名称
     * @param listener  用户变更监听
     * @param users     用户
     * @return FTP服务器
     */
    public static FtpServer createServer(int port, String adminName,
                                         FtpUserManager.OnUserChangedListener listener,
                                         FtpUser... users) {
        final FtpServerFactory factory = new FtpServerFactory();
        final ListenerFactory lf = new ListenerFactory();
        lf.setPort(port);
        lf.setDataConnectionConfiguration(
                new DataConnectionConfigurationFactory().createDataConnectionConfiguration());
        factory.addListener("default", lf.createListener());
        final FtpUserManagerFactory umf = FtpUserManagerFactory.getInstance();
        umf.reset();
        if (adminName != null)
            umf.setAdminName(adminName);
        umf.setOnUserChangedListener(listener);
        umf.setUsers(users);
        final FtpUserManager um = umf.createUserManager();
        factory.setUserManager(um);
        factory.setFileSystem(new FtpFileSystemFactory());
        final ConnectionConfigFactory ccf = new ConnectionConfigFactory();
        ccf.setAnonymousLoginEnabled(um.isAnonymousLoginEnabled());
        factory.setConnectionConfig(ccf.createConnectionConfig());
        return new FtpServer(factory.createServer());
    }

    /**
     * 创建FTP服务器
     *
     * @param port      端口
     * @param adminName 管理员名称
     * @param listener  用户变更监听
     * @param users     用户
     * @return FTP服务器
     */
    public static FtpServer createServer(int port, String adminName,
                                         FtpUserManager.OnUserChangedListener listener,
                                         Collection<? extends FtpUser> users) {
        final FtpServerFactory factory = new FtpServerFactory();
        final ListenerFactory lf = new ListenerFactory();
        lf.setPort(port);
        lf.setDataConnectionConfiguration(
                new DataConnectionConfigurationFactory().createDataConnectionConfiguration());
        factory.addListener("default", lf.createListener());
        final FtpUserManagerFactory umf = FtpUserManagerFactory.getInstance();
        if (adminName != null)
            umf.setAdminName(adminName);
        umf.setOnUserChangedListener(listener);
        umf.setUsers(users);
        final FtpUserManager um = umf.createUserManager();
        factory.setUserManager(um);
        factory.setFileSystem(new FtpFileSystemFactory());
        final ConnectionConfigFactory ccf = new ConnectionConfigFactory();
        ccf.setAnonymousLoginEnabled(um.isAnonymousLoginEnabled());
        factory.setConnectionConfig(ccf.createConnectionConfig());
        return new FtpServer(factory.createServer());
    }

    /**
     * 创建FTP服务器
     *
     * @param port          端口
     * @param homeDirectory 根目录路径
     * @return FTP服务器
     */
    public static FtpServer createServer(int port, File homeDirectory) {
        final FtpServerFactory factory = new FtpServerFactory();
        final ListenerFactory lf = new ListenerFactory();
        lf.setPort(port);
        lf.setDataConnectionConfiguration(
                new DataConnectionConfigurationFactory().createDataConnectionConfiguration());
        factory.addListener("default", lf.createListener());
        final FtpUserManagerFactory umf = FtpUserManagerFactory.getInstance();
        umf.setAnonymousEnable(new FileFtpFileSystemViewAdapter(homeDirectory));
        factory.setUserManager(umf.createUserManager());
        factory.setFileSystem(new FtpFileSystemFactory());
        factory.setConnectionConfig(new ConnectionConfigFactory().createConnectionConfig());
        return new FtpServer(factory.createServer());
    }

    /**
     * 创建FTP服务器
     *
     * @param port          端口
     * @param homeDirectory 根目录路径
     * @return FTP服务器
     */
    public static FtpServer createServer(int port, String homeDirectory) {
        final FtpServerFactory factory = new FtpServerFactory();
        final ListenerFactory lf = new ListenerFactory();
        lf.setPort(port);
        lf.setDataConnectionConfiguration(
                new DataConnectionConfigurationFactory().createDataConnectionConfiguration());
        factory.addListener("default", lf.createListener());
        final FtpUserManagerFactory umf = FtpUserManagerFactory.getInstance();
        umf.setAnonymousEnable(new FileFtpFileSystemViewAdapter(homeDirectory));
        factory.setUserManager(umf.createUserManager());
        factory.setFileSystem(new FtpFileSystemFactory());
        factory.setConnectionConfig(new ConnectionConfigFactory().createConnectionConfig());
        return new FtpServer(factory.createServer());
    }

    /**
     * 创建FTP服务器
     *
     * @param port          端口
     * @param context       Context
     * @param homeDirectory 根目录路径
     * @return FTP服务器
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static FtpServer createServer(int port, Context context, Uri homeDirectory) {
        final FtpServerFactory factory = new FtpServerFactory();
        final ListenerFactory lf = new ListenerFactory();
        lf.setPort(port);
        lf.setDataConnectionConfiguration(
                new DataConnectionConfigurationFactory().createDataConnectionConfiguration());
        factory.addListener("default", lf.createListener());
        final FtpUserManagerFactory umf = FtpUserManagerFactory.getInstance();
        umf.setAnonymousEnable(new UriFtpFileSystemViewAdapter(context, homeDirectory));
        factory.setUserManager(umf.createUserManager());
        factory.setFileSystem(new FtpFileSystemFactory());
        factory.setConnectionConfig(new ConnectionConfigFactory().createConnectionConfig());
        return new FtpServer(factory.createServer());
    }
}
