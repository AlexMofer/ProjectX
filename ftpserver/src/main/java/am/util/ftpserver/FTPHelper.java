/*
 * Copyright (C) 2017 AlexMofer
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

import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;

import java.util.Collection;

/**
 * FTP辅助器
 * Created by Alex on 2017/12/21.
 */
@SuppressWarnings("unused")
public class FTPHelper {

    private FTPHelper() {
        //no instance
    }

    public static FtpServer createServer(int port, int maxLoginFailures, int loginFailureDelay,
                                         boolean anonymousEnable, String anonymousHomeDirectory,
                                         FTPUser... users) {
        final ListenerFactory listener = new ListenerFactory();
        listener.setPort(port);
        listener.setDataConnectionConfiguration(
                new DataConnectionConfigurationFactory().createDataConnectionConfiguration());

        ConnectionConfigFactory connection = new ConnectionConfigFactory();
        connection.setMaxLoginFailures(maxLoginFailures);
        connection.setLoginFailureDelay(loginFailureDelay);
        connection.setAnonymousLoginEnabled(anonymousEnable);

        final FtpServerFactory server = new FtpServerFactory();
        server.setUserManager(new FTPUserManager(anonymousEnable, anonymousHomeDirectory, users));
        server.setFileSystem(FTPFileSystemFactory.getInstance());
        server.addListener("default", listener.createListener());
        server.setConnectionConfig(connection.createConnectionConfig());

        return server.createServer();
    }

    public static FtpServer createServer(int port, int maxLoginFailures, int loginFailureDelay,
                                         boolean anonymousEnable, String anonymousHomeDirectory,
                                         Collection<? extends FTPUser> users) {
        final ListenerFactory listener = new ListenerFactory();
        listener.setPort(port);
        listener.setDataConnectionConfiguration(
                new DataConnectionConfigurationFactory().createDataConnectionConfiguration());

        ConnectionConfigFactory connection = new ConnectionConfigFactory();
        connection.setMaxLoginFailures(maxLoginFailures);
        connection.setLoginFailureDelay(loginFailureDelay);
        connection.setAnonymousLoginEnabled(anonymousEnable);

        final FtpServerFactory server = new FtpServerFactory();
        server.setUserManager(new FTPUserManager(anonymousEnable, anonymousHomeDirectory, users));
        server.setFileSystem(FTPFileSystemFactory.getInstance());
        server.addListener("default", listener.createListener());
        server.setConnectionConfig(connection.createConnectionConfig());

        return server.createServer();
    }
}
