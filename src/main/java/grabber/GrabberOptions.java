package grabber;

public class GrabberOptions {
    private String nextChapterBtn = "NOT_SET";
    private String nextChapterURL;
    private boolean reGrab;
    private boolean useHeadless = false;
    private boolean useAccount = false;
    private boolean autoDetectContainer = false;
    private boolean reverseChapterOrder = false;
    private boolean retryFailedChapters = true;
    private int retryAttempts = 1;
    private int waitTime = 0;

    public GrabberOptions() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder modifier(GrabberOptions options) {
        return new Builder(options);
    }

    public String getNextChapterBtn() {
        return nextChapterBtn;
    }

    public String getNextChapterURL() {
        return nextChapterURL;
    }

    public boolean isReGrab() {
        return reGrab;
    }

    public boolean isUseHeadless() {
        return useHeadless;
    }

    public boolean isUseAccount() {
        return useAccount;
    }

    public boolean isAutoDetectContainer() {
        return autoDetectContainer;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public boolean isRetryFailedChapters() {
        return retryFailedChapters;
    }

    public int getRetryAttempts() {
        return retryAttempts;
    }

    public static final class Builder {
        private GrabberOptions options;

        public Builder() {
            options = new GrabberOptions();
        }

        public Builder(GrabberOptions options) {
            this.options = options;
        }

        public Builder nextChapterBtn(String nextChapterBtn) {
            options.nextChapterBtn = nextChapterBtn;
            return this;
        }

        public Builder nextChapterURL(String nextChapterURL) {
            options.nextChapterURL = nextChapterURL;
            return this;
        }

        public Builder reGrab(boolean reGrab) {
            options.reGrab = reGrab;
            return this;
        }

        public Builder useHeadless(boolean useHeadless) {
            options.useHeadless = useHeadless;
            return this;
        }

        public Builder useAccount(boolean useAccount) {
            options.useAccount = useAccount;
            return this;
        }

        public Builder autoDetectContainer(boolean autoDetectContainer) {
            options.autoDetectContainer = autoDetectContainer;
            return this;
        }

        public Builder waitTime(int waitTime) {
            options.waitTime = waitTime;
            return this;
        }

        public Builder reverseChapterOrder(boolean reverseChapterOrder) {
            options.reverseChapterOrder = reverseChapterOrder;
            return this;
        }

        public Builder retryFailedChapters(boolean retryFailedChapters) {
            options.retryFailedChapters = retryFailedChapters;
            return this;
        }

        public Builder retryAttempts(int retryAttempts) {
            options.retryAttempts = retryAttempts;
            return this;
        }

        public GrabberOptions build() {
            return options;
        }
    }
}

