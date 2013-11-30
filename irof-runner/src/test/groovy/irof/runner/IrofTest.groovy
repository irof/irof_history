package irof.runner

import groovy.transform.InheritConstructors
import org.junit.Before
import org.junit.Test
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.FrameworkMethod

import static org.mockito.Mockito.*

class IrofTest {
    @InheritConstructors
    class MockIrof extends Irof{
        protected void originalRunChild(FrameworkMethod method, RunNotifier notifier){
            // do nothing
        }
    }
    Irof irof = new MockIrof(IrofTest)
    FrameworkMethod method = mock(FrameworkMethod)
    RunNotifier notifier = mock(RunNotifier)

    @Before
    void setUp(){
        Irof.MAX_IROF_COUNT.times{
            irof.runChild(method, notifier)
        }
    }

    @Test(expected = IrofTiredException)
    void "should failed at 163rd test"(){
        irof.runChild(method, notifier)
    }
}
