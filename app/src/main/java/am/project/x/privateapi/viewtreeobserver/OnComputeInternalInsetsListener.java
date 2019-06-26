package am.project.x.privateapi.viewtreeobserver;

/**
 * Interface definition for a callback to be invoked when layout has
 * completed and the client can compute its interior insets.
 * Created by Alex on 2019/6/11.
 */
public interface OnComputeInternalInsetsListener {

    /**
     * Callback method to be invoked when layout has completed and the
     * client can compute its interior insets.
     *
     * @param inoutInfo Should be filled in by the implementation with
     *                  the information about the insets of the window.  This is called
     *                  with whatever values the previous OnComputeInternalInsetsListener
     *                  returned, if there are multiple such listeners in the window.
     */
    void onComputeInternalInsets(InternalInsetsInfo inoutInfo);
}