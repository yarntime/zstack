package org.zstack.test.integration.core

import org.zstack.testlib.SubCase

/**
 * Created by lining on 2017/5/14.
 */
class RetryInSecsCase extends SubCase {

    @Override
    void clean() {
    }

    @Override
    void setup() {
    }

    @Override
    void environment() {
    }

    @Override
    void test() {
        baseTest()
        retryTimeTest()
    }

    void baseTest(){
        // do nothing, expect true
        assert retryInSecs(2){
        }

        // return a not boolean object, expect false
        assert !retryInSecs(2){
            return new Object()
        }

        assert retryInSecs(2){
            return new Boolean(true)
        }

        assert retryInSecs(){
            return true
        }

        assert !retryInSecs(2){
            return false
        }

        retryInSecs(3){
            assert 1 == 1
            assert 2 == 2
            assert 1000 * 1 == 100 * 10
            assert true == true
            assert null != new Object()
            assert "love you, zstack, best wish for you!" == "love you, zstack, best wish for you!"
        }
    }

    void retryTimeTest(){

        // case
        int retryTimes = 3
        long startTime = System.currentTimeMillis()
        int actualExecutionTimes = 0
        assert retryInSecs(retryTimes){
            actualExecutionTimes ++
            return true
        }
        long endTime = System.currentTimeMillis()
        assert endTime - startTime < 1000
        assert 1 == actualExecutionTimes

        // case
        retryTimes = 2
        long currentTime = 0
        startTime = System.currentTimeMillis()
        actualExecutionTimes = 0
        retryInSecs(retryTimes){
            actualExecutionTimes ++
            if(retryTimes - currentTime == 1){
                assert true
            }else{
                currentTime ++
                assert false
            }
        }
        endTime = System.currentTimeMillis()
        assert endTime - startTime > 1000 * (retryTimes -1)
        // Invalid assert, The maximum length of time cannot be determined
        // assert endTime - startTime < 1000 * (retryTimes)
        assert retryTimes == actualExecutionTimes

        // case
        retryTimes = 3
        startTime = System.currentTimeMillis()
        actualExecutionTimes = 0
        try {
            retryInSecs(retryTimes){
                actualExecutionTimes ++
                assert new Object() == new Object().class
                assert 1 == 2
            }
            assert false
        }catch (Throwable e){
            endTime = System.currentTimeMillis()
            assert endTime - startTime > 1000 * (retryTimes -1)
            // Invalid assert, The maximum length of time cannot be determined
            // assert endTime - startTime < 1000 * (retryTimes)
            assert retryTimes == actualExecutionTimes
        }

        // case
        retryTimes = 2
        startTime = System.currentTimeMillis()
        actualExecutionTimes = 0
        assert !retryInSecs(retryTimes){
            actualExecutionTimes ++
            return false
        }
        endTime = System.currentTimeMillis()
        assert endTime - startTime > 1000 * (retryTimes)
        // Invalid assert, The maximum length of time cannot be determined
        // assert endTime - startTime < 1000 * (retryTimes + 1)
        assert retryTimes == actualExecutionTimes
    }
}
