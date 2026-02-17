package co.com.pragma.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void testConstantsValues() {
        assertNotNull(Constants.FRANCHISE_TABLE_NAME);
        assertNotNull(Constants.BRANCH_TABLE_NAME);
        assertNotNull(Constants.PRODUCT_TABLE_NAME);
        assertNotNull(Constants.FRANCHISE_PARTITION_KEY);
        assertNotNull(Constants.BRANCH_PARTITION_KEY);
        assertNotNull(Constants.PRODUCT_PARTITION_KEY);
        assertNotNull(Constants.ERROR_FRANCHISE_NOT_FOUND);
        assertNotNull(Constants.ERROR_BRANCH_NOT_FOUND);
        assertNotNull(Constants.ERROR_PRODUCT_NOT_FOUND);
        assertNotNull(Constants.ERROR_INVALID_STOCK);
        assertNotNull(Constants.ERROR_INVALID_NAME);
        assertNotNull(Constants.API_BASE_PATH);
        assertNotNull(Constants.PATH_ID);
        assertNotNull(Constants.PATH_BRANCHES);
        assertNotNull(Constants.PATH_BRANCH_ID);
        assertNotNull(Constants.PATH_PRODUCTS);
        assertNotNull(Constants.PATH_PRODUCT_ID);
        assertNotNull(Constants.PATH_STOCK);
        assertNotNull(Constants.PATH_MAX_STOCK);
        assertNotNull(Constants.PATH_FRANCHISE_ID);
        assertNotNull(Constants.PATH_PRODUCT_BY_NAME);
    }

    @Test
    void testConstantsCannotBeInstantiated() throws Exception {
        java.lang.reflect.Constructor<Constants> constructor = Constants.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(UnsupportedOperationException.class, () -> instantiateConstants(constructor));
    }

    private static void instantiateConstants(java.lang.reflect.Constructor<Constants> constructor) {
        try {
            constructor.newInstance();
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof UnsupportedOperationException) {
                throw (UnsupportedOperationException) cause;
            }
            throw new RuntimeException(cause);
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
