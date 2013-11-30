package irof.runner

import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.InitializationError

class Irof extends BlockJUnit4ClassRunner {
    static final int MAX_IROF_COUNT = 162
    static int testCount = 0

    Irof(Class<?> klass) throws InitializationError {
        super(klass)
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        testCount++
        if(testCount > MAX_IROF_COUNT){
            throw new IrofTiredException()
        }
        originalRunChild(method, notifier)
    }

    protected void originalRunChild(FrameworkMethod method, RunNotifier notifier){
        super.runChild(method, notifier)
    }
}
