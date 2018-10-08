// ###WS@M Project:PDFelement ###
package am.project.x.business.others.ftp;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;

import am.project.x.R;

/**
 * 文件传输快捷菜单
 */
@RequiresApi(24)
public class FTPTileService extends TileService {

    @Override
    public void onClick() {
        super.onClick();
        final boolean active;
        if (FTPService.isStarted()) {
            FTPService.stop(this);
            active = false;
        } else {
            FTPService.start(this, 0);
            active = true;
        }
        updateTile(active);
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile(FTPService.isStarted());
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        updateTile(FTPService.isStarted());
    }

    private void updateTile(boolean active) {
        final Tile tile = getQsTile();
        if (active) {
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