package grabber.listeners;

import grabber.listeners.events.DownloadFinishEvent;
import grabber.listeners.events.DownloadStopEvent;

public interface DownloadStatusListener {

    void downloadStopped(DownloadStopEvent e);

    void downloadFinished(DownloadFinishEvent e);
}
