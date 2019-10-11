/*
 * Copyright (C) 2018 AlexMofer
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

package am.project.x.business.others.ftp;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import am.project.x.R;

import androidx.annotation.RequiresApi;

/**
 * 文件传输快捷菜单
 */
@RequiresApi(24)
public class FtpTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        final FtpServiceHelper helper = FtpServiceHelper.getInstance();
        if (helper.isStarted())
            helper.stop(this);
        else
            helper.start(this);
        updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        updateTile();
    }

    private void updateTile() {
        final Tile tile = getQsTile();
        if (FtpServiceHelper.getInstance().isStarted()) {
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_ftp));
            tile.setLabel(getString(R.string.ftp_tile_label_active));
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            tile.setIcon(Icon.createWithResource(this, R.drawable.ic_tile_ftp));
            tile.setLabel(getString(R.string.ftp_tile_label_inactive));
            tile.setState(Tile.STATE_INACTIVE);
        }
        tile.updateTile();
    }
}