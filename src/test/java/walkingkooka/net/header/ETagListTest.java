package walkingkooka.net.header;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.ListTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ETagListTest implements ListTesting2<ETagList, ETag>,
    ClassTesting<ETagList>,
    ImmutableListTesting<ETagList, ETag> {

    private final static ETag ETAG1 = ETag.strong("strong111");

    private final static ETag ETAG2 = ETag.weak("weak222");

    // list.............................................................................................................

    @Test
    public void testGet() {
        this.getAndCheck(
            this.createList(),
            0, // index
            ETAG1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            ETAG2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            ETAG1 // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final ETagList list = this.createList();

        this.removeIndexFails(
            list,
            0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final ETagList list = this.createList();

        this.removeFails(
            list,
            list.get(0)
        );
    }

    // setElements......................................................................................................

    @Test
    public void testWithDoesntDoubleWrap() {
        final ETagList list = this.createList();
        assertSame(
            list,
            list.setElements(list)
        );
    }

    @Test
    public void testSetElementsWithEmpty() {
        assertSame(
            ETagList.EMPTY,
            new ETagList(
                Lists.of(
                    ETAG1,
                    ETAG2
                )
            ).setElements(Lists.empty())
        );
    }

    // replace..........................................................................................................

    @Test
    public void testReplaceWithNullFails() {
        final ETagList etags = this.createList();

        assertThrows(
            NullPointerException.class,
            () -> etags.replace(
                1,
                null
            )
        );
    }

    @Override
    public ETagList createList() {
        return new ETagList(
            Lists.of(
                ETAG1,
                ETAG2
            )
        );
    }

    // firstOrEmpty.....................................................................................................

    @Test
    public void testFirstOrEmptyWhenEmpty() {
        this.firstOrEmptyAndCheck(
            ETagList.EMPTY
        );
    }

    @Test
    public void testFirstOrEmptyWhenNotEmpty() {
        this.firstOrEmptyAndCheck(
            ETagList.EMPTY.concat(ETAG1)
                .concat(ETAG2),
            ETAG1
        );
    }

    // class............................................................................................................

    @Override
    public Class<ETagList> type() {
        return ETagList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
