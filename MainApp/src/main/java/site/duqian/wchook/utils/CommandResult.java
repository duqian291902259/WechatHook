package site.duqian.wchook.utils;

/**
 * result of command
 */
public  class CommandResult {

    //CommandResult{result=0, successMsg='Android Debug Bridge version 1.0.31', errorMsg=''}
    public int result = -1;
    public String successMsg;
    public String errorMsg;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public CommandResult(int result) {
        this.result = result;
    }


    public CommandResult(int result, String successMsg, String errorMsg) {
        this.result = result;
        this.successMsg = successMsg;
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "CommandResult{" +
                "result=" + result +
                ", successMsg='" + successMsg + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}