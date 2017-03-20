package sample;

import javafx.concurrent.Task;

import java.io.File;

public class SendFileTask extends Task<Void> {
    File file;
    int progress, state;
    boolean stateChanged;

    public SendFileTask(File file) {
        this.file = file;
        this.progress = 0;
        this.state = 0;
        this.stateChanged = true;
    }

    @Override protected Void call() throws Exception {
        while (true) {
            if (stateChanged) {
                switch (state) {
                    case 0: updateMessage("Idle."); break;
                    case 1: updateMessage("In progress..."); break;
                    case 2: updateMessage("Done."); break;
                    default: updateMessage("Undefined state."); break;
                }
                stateChanged = false;
            }

            //...wysyłanie pliku...

            updateProgress(progress, file.length());
            if (progress == file.length()) {
                updateMessage("Done.");
                state = 2;
                stateChanged = true;
                return null;
            }
        }
    }
}
