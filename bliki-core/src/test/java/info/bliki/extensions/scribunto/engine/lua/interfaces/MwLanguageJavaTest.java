package info.bliki.extensions.scribunto.engine.lua.interfaces;

import org.junit.Test;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class MwLanguageJavaTest {
    @Test public void testGetValidLocale() throws Exception {
        Locale locale = MwLanguage.getLocale(LuaValue.valueOf("fr"));
        assertThat(locale.getLanguage()).isEqualTo("fr");
    }

    @Test public void testGetInvalidLocaleThrowsError() throws Exception {
        try {
            MwLanguage.getLocale(LuaValue.valueOf("[[bogus]]"));
            fail("expected LuaError");
        } catch (LuaError e) {
            assertThat(e.getMessage()).isEqualTo("language code '[[bogus]]' is invalid");
        }
    }
}
