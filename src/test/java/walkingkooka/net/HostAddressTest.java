/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.net;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.text.CharSequences;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public final class HostAddressTest implements ClassTesting2<HostAddress>,
    ComparableTesting2<HostAddress>,
    ThrowableTesting,
    ToStringTesting<HostAddress> {

    private final static String HOST = "example.com";

    // tests

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> HostAddress.with(null)
        );
    }

    @Test
    public void testWithEmptyFails() {
        this.withFails("");
    }

    @Test
    public void testWithTooLongFails() {
        final char[] array = new char[HostAddress.MAX_LENGTH];
        Arrays.fill(array, 'a');
        final String address = new String(array);
        this.withFails(
            address,
            HostAddress.invalidHostLength(array.length)
        );
    }

    @Test
    public void testWithInvalidNameStartsWithDashFails() {
        final String address = "-invalid.last";
        this.withFails(
            address,
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testWithInvalidNameStartsWithDash2Fails() {
        final String address = "first.-invalid.last";
        this.withFails(
            address,
            HostAddressProblemInvalidCharacter.with(
                address.indexOf('-')
            )
        );
    }

    @Test
    public void testWithInvalidNameStartsWithDotFails() {
        final String address = ".invalid";
        this.withFails(
            address,
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testWithIp4WithInvalidValue() {
        final String address = "1.2.345.6";
        this.withFails(
            address,
            HostAddressProblemInvalidValue.with(
                address.indexOf('3')
            )
        );
    }

    @Test
    public void testWithIp6WithInvalidValueFails() {
        final String address = "1:2:34567:4:5:6:7:8";
        this.withFails(
            address,
            HostAddressProblemInvalidValue.with(
                address.indexOf('3')
            )
        );
    }

    private void withFails(final String address) {
        this.withFails(
            address,
            (String) null
        );
    }

    private void withFails(final String address,
                           final HostAddressProblem problem) {
        this.withFails(
            address,
            problem.message(address)
        );
    }

    private void withFails(final String address,
                           final String message) {
        final Exception expected = assertThrows(
            RuntimeException.class,
            () -> HostAddress.with(address)
        );
        if (null != message) {
            checkMessage(expected, message);
        }
    }

    @Test
    public void testWithReallyShort() {
        final String address = "a";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        checkEquals("values", new byte[0], hostAddress.values());
        this.nameCheck(hostAddress);
        this.checkNotIpAddress(hostAddress);
    }

    @Test
    public void testName() {
        final String address = "address";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        checkEquals("values", new byte[0], hostAddress.values());
        this.nameCheck(hostAddress);
        this.checkNotIpAddress(hostAddress);
    }

    @Test
    public void testNameWithDigits() {
        final String address = "address123";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        checkEquals("values", new byte[0], hostAddress.values());
        this.nameCheck(hostAddress);
        this.checkNotIpAddress(hostAddress);
    }

    @Test
    public void testNameWithDash() {
        final String address = "address-after";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        checkEquals("values", new byte[0], hostAddress.values());
        this.nameCheck(hostAddress);
        this.checkNotIpAddress(hostAddress);
    }

    @Test
    public void testNameWithSubDomains() {
        final String address = "sub.address";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        checkEquals("values", new byte[0], hostAddress.values());
        this.nameCheck(hostAddress);
        this.checkNotIpAddress(hostAddress);
    }

    @Test
    public void testNameThatsStartsOfLikeIp4() {
        final String address = "1.2.3.4.5.address";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        checkEquals("values", new byte[0], hostAddress.values());
        this.nameCheck(hostAddress);
        this.checkNotIpAddress(hostAddress);
    }

    @Test
    public void testNameWithInvalidOctetValue() {
        final String address = "1234.address";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        this.valuesCheck(hostAddress, new byte[0]);
        this.nameCheck(hostAddress);
        this.checkNotIpAddress(hostAddress);
    }

    @Test
    public void testNameWithInvalidOctetValue2() {
        final String address = "1.2.3456.address";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        this.valuesCheck(hostAddress, new byte[0]);
        this.nameCheck(hostAddress);
        this.checkNotIpAddress(hostAddress);
    }

    @Test
    public void testWithIp4Address() {
        final String address = "1.2.3.4";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        this.valuesCheck(hostAddress, new byte[]{1, 2, 3, 4});
        this.checkIp4Address(hostAddress);
    }

    @Test
    public void testWithIp4Address2() {
        final String address = "123.45.67.255";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        this.valuesCheck(hostAddress, new byte[]{123, 45, 67, (byte) 255});
        this.checkIp4Address(hostAddress);
    }

    @Test
    public void testWithIp6Address() {
        final String address = "1:2:3:4:5:6:7:8";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        this.valuesCheck(hostAddress, "00010002000300040005000600070008");
        this.checkIp6Address(hostAddress);
    }

    @Test
    public void testWithIp6Address2() {
        final String address = "1111:2222:3333:4444:5555:6666:7777:8888";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        this.valuesCheck(hostAddress, "11112222333344445555666677778888");
        this.checkIp6Address(hostAddress);
    }

    @Test
    public void testWithIp6AddressWithEmpty() {
        final String address = "1111::4444:5555:6666:7777:8888";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        this.valuesCheck(hostAddress, "11110000000044445555666677778888");
        this.checkIp6Address(hostAddress);
    }

    @Test
    public void testWithIp6AddressWithIp4() {
        final String address = "1:2:3:4:5:6:255.7.8.9";
        final HostAddress hostAddress = HostAddress.with(address);
        this.valueCheck(hostAddress, address);
        this.valuesCheck(hostAddress, "000100020003000400050006ff070809");
        this.checkIp6Address(hostAddress);
    }

    private void valueCheck(final HostAddress hostAddress,
                            final String address) {
        this.checkEquals(
            address,
            hostAddress.value(),
            "address"
        );
    }

    private void valuesCheck(final HostAddress hostAddress,
                             final String expected) {
        this.valuesCheck(
            hostAddress,
            toByteArray(expected)
        );
    }

    private void valuesCheck(final HostAddress hostAddress,
                             final byte[] expected) {
        checkEquals("values", hostAddress.values(), expected);
    }

    private void nameCheck(final HostAddress hostAddress) {
        assertTrue(hostAddress.isName(), "is name=" + hostAddress);
    }

    private void checkNotIpAddress(final HostAddress hostAddress) {
        this.nameCheck(hostAddress);
        checkEquals(
            null,
            hostAddress.isIpAddress(),
            () -> hostAddress + " should have an IpAddress"
        );
    }

    private void checkIp4Address(final HostAddress address) {
        assertFalse(address.isName(), address + " should not be a name");
        checkEquals(
            true,
            address.isIpAddress()
                .isIp4(),
            () -> address + " should have an Ip4Address"
        );
    }

    private void checkIp6Address(final HostAddress address) {
        checkEquals(
            false,
            address.isName(),
            () -> address + " should not be a name"
        );
        checkEquals(
            true,
            address.isIpAddress().isIp6(),
            () -> address + " should have an Ip6Address"
        );
    }

    // HashCodeEqualsDefined ...........................................................................................

    @Test
    public void testHashCodeCaseUnimportant() {
        this.checkEqualsAndHashCode(HostAddress.with(HOST.toUpperCase()));
    }

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(HostAddress.with("different"));
    }

    // parse ...........................................................................................................

    @Test
    public void testParseProbablyIp6Fails() {
        this.parseStringFails(
            "a.b:c",
            HostAddressProblemProbablyIp6.INSTANCE
        );
    }

    @Test
    public void testParseDotAtStartFails() {
        this.parseStringFails(
            ".n",
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testParseDotAtEndFails() {
        this.parseStringFails(
            "n.",
            HostAddressProblemIncomplete.INSTANCE
        );
    }

    @Test
    public void testParseDashAtStartFails() {
        this.parseStringFails(
            "-n",
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testParseDashAtEndFails() {
        final String name = "n-";
        this.parseStringFails(
            name,
            HostAddressProblemInvalidCharacter.with(name.indexOf('-'))
        );
    }

    @Test
    public void testParseOneLabelInvalidAtStartFails() {
        this.parseStringFails(
            "!b",
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testParseOneLabelInvalidAtStarts2Fails() {
        this.parseStringFails(
            "!bc",
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testParseOneLabelInvalidFails() {
        this.parseStringFails(
            "n!ame",
            HostAddressProblemInvalidCharacter.with(1)
        );
    }

    @Test
    public void testParseOneLabelEndsWithInvalidFails() {
        this.parseStringFails(
            "a!",
            HostAddressProblemInvalidCharacter.with(1)
        );
    }

    @Test
    public void testParseOneLabelEndsWithInvalid2Fails() {
        this.parseStringFails(
            "ab!",
            HostAddressProblemInvalidCharacter.with(2)
        );
    }

    @Test
    public void testParseDashThenDotFails() {
        final String address = "exampl-.com";
        this.parseStringFails(
            address,
            HostAddressProblemInvalidCharacter.with(address.indexOf('-'))
        );
    }

    @Test
    public void testParseSecondLabelInvalidAtStartFails() {
        final String name = "a.!b";
        this.parseStringFails(
            name,
            HostAddressProblemInvalidCharacter.with(name.indexOf('!'))
        );
    }

    @Test
    public void testParseSecondLabelInvalidFails() {
        final String name = "a.b!c.d";
        this.parseStringFails(
            name,
            HostAddressProblemInvalidCharacter.with(name.indexOf('!'))
        );
    }

    @Test
    public void testParseSecondLabelEndsWithInvalidFails() {
        final String name = "a.b!.c";
        this.parseStringFails(
            name,
            HostAddressProblemInvalidCharacter.with(name.indexOf('!'))
        );
    }

    @Test
    public void testParseSecondLabelEndsWithDashFails() {
        final String name = "a.b-.c";
        this.parseStringFails(
            name,
            HostAddressProblemInvalidCharacter.with(name.indexOf('-'))
        );
    }

    @Test
    public void testParseLastLabelEndWithInvalidFails() {
        final String name = "a.b!";
        this.parseStringFails(
            name,
            HostAddressProblemInvalidCharacter.with(name.indexOf('!'))
        );
    }

    @Test
    public void testParseLastLabelEndsWithDashFails() {
        final String name = "a.b-";
        this.parseStringFails(
            name,
            HostAddressProblemInvalidCharacter.with(name.indexOf('-'))
        );
    }

    @Test
    public void testParseIp4PartialFails() {
        final String name = "1.2";
        this.parseStringFails(
            name,
            HostAddressProblemProbablyIp4.INSTANCE
        );
    }

    @Test
    public void testParseIp4Fails() {
        final String name = "1.2.3.4";
        this.parseStringFails(
            name,
            HostAddressProblemProbablyIp4.INSTANCE
        );
    }

    @Test
    public void testParseIp4WithExtraOctetsFails() {
        final String name = "1.2.3.4.5.6";
        this.parseStringFails(
            name,
            HostAddressProblemProbablyIp4.INSTANCE
        );
    }

    @Test
    public void testParseIp6Fails() {
        final String name = "1:2:3:4:5:6:7:8";
        this.parseStringFails(
            name,
            HostAddressProblemProbablyIp6.INSTANCE
        );
    }

    @Test
    public void testParseLabelTooLongFails() {
        final char[] array = new char[HostAddress.MAX_LABEL_LENGTH];
        Arrays.fill(array, 'x');
        final String name = new String(array);
        this.parseStringFails(
            name,
            HostAddressProblemInvalidLength.with(0)
        );
    }

    @Test
    public void testParseLabelTooLongFirstFails() {
        final char[] array = new char[HostAddress.MAX_LABEL_LENGTH];
        Arrays.fill(array, 'x');
        final String name = new String(array);
        this.parseStringFails(
            name + ".second",
            HostAddressProblemInvalidLength.with(0)
        );
    }

    @Test
    public void testParseLabelTooLongSecondFails() {
        final char[] array = new char[HostAddress.MAX_LABEL_LENGTH];
        Arrays.fill(array, 'x');
        this.parseStringFails(
            "first." + new String(array) + ".last",
            HostAddressProblemInvalidLength.with("first.".length())
        );
    }

    @Test
    public void testParseLabelTooLongLastFails() {
        final char[] array = new char[HostAddress.MAX_LABEL_LENGTH];
        Arrays.fill(array, 'x');
        this.parseStringFails(
            "first." + new String(array),
            HostAddressProblemInvalidLength.with("first.".length())
        );
    }

    @Test
    public void testParseOneLetter() {
        this.parseStringAndCheck("A");
    }

    @Test
    public void testParseOneLetterWithStartAndEnd() {
        this.parseStringAndCheck(
            "!B!",
            1,
            2
        );
    }

    @Test
    public void testParseOneLabel() {
        this.parseStringAndCheck("one");
    }

    @Test
    public void testParseOneLabelWithStartAndEnd() {
        this.parseStringAndCheck(
            "!one!",
            1,
            4
        );
    }

    @Test
    public void testParseManyLabels() {
        this.parseStringAndCheck("first.second");
    }

    @Test
    public void testParseManyLabelsWithStartAndEnd() {
        final String address = "!first.second!";
        this.parseStringAndCheck(
            address,
            1,
            address.length() - 1
        );
    }

    @Test
    public void testParseManyLabels2() {
        this.parseStringAndCheck("first.second.third");
    }

    @Test
    public void testParseManyLabels2WithStartAndEnd() {
        final String address = "!first.second.third!";
        this.parseStringAndCheck(address, 1, address.length() - 1);
    }

    private void parseStringAndCheck(final String address) {
        this.parseStringAndCheck(address, 0, address.length());
    }

    private void parseStringAndCheck(final String address,
                                     final int start,
                                     final int end) {
        this.checkEquals(
            null,
            HostAddress.tryParseName(
                address,
                start,
                end
            )
        );
    }

    private void parseStringFails(final String address,
                                  final HostAddressProblem problem) {
        this.parseStringFails(
            address,
            0,
            address.length(),
            problem
        );
    }

    private void parseStringFails(final String address,
                                  final int start,
                                  final int end,
                                  final HostAddressProblem problem) {
        this.checkEquals(
            problem,
            HostAddress.tryParseName(
                address,
                start,
                end
            ),
            "problem"
        );
    }

    // parseIp4.........................................................................................................

    @Test
    public void testParseIp4ActuallyIp6Fails() {
        this.parseIp4Fails(
            "1234:2:3:4:5:6:7",
            false, // insideIp6
            HostAddressProblemProbablyIp6.INSTANCE
        );
    }

    @Test
    public void testParseIp4ActuallyIp6WithHexDigitCharacterFails() {
        this.parseIp4Fails(
            "A234:2:3:4:5:6:7",
            false, // insideIp6
            HostAddressProblemProbablyIp6.INSTANCE
        );
    }

    @Test
    public void testParseIp4ActuallyIp6WithHexDigitCharacter2Fails() {
        this.parseIp4Fails(
            "a234:2:3:4:5:6:7",
            false, // insideIp6
            HostAddressProblemProbablyIp6.INSTANCE
        );
    }

    @Test
    public void testParseIp4Ip4WithColonFails() {
        this.parseIp4Fails(
            "1:2.3.4",
            true, // insideIp6
            HostAddressProblemInvalidCharacter.with(1)
        );
    }

    @Test
    public void testParseIp4FirstOctetEmptyFails() {
        this.parseIp4Fails(
            ".2.3.4",
            false, // insideIp6
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testParseIp4SecondOctetEmptyFails() {
        this.parseIp4Fails(
            "1..3.4",
            false, // insideIp6
            HostAddressProblemInvalidCharacter.with(2)
        );
    }

    @Test
    public void testParseIp4ThirdOctetEmptyFails() {
        this.parseIp4Fails(
            "1.2..4",
            false, // insideIp6
            HostAddressProblemInvalidCharacter.with(4)
        );
    }

    @Test
    public void testParseIp4LastOctetEmptyFails() {
        final String address = "1.2.3.";
        this.parseIp4Fails(
            address,
            false, // insideIp6
            HostAddressProblemInvalidCharacter.with(
                address.lastIndexOf('.')
            )
        );
    }

    @Test
    public void testParseIp4FirstOctetStartsWithAlphaFails() {
        this.parseIp4Fails(
            "a.2.3.4"
            , false, // insideIp6
            HostAddressProblemProbablyIp6.INSTANCE
        );
    }

    @Test
    public void testParseIp4FirstOctetStartsWithInvalidFails() {
        this.parseIp4Fails(
            "!.2.3.4",
            false, // insideIp6
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testParseIp4FirstOctetIncludesAlphaFails() {
        this.parseIp4Fails(
            "1a.2.3.4",
            false, // insideIp6
            HostAddressProblemProbablyIp6.INSTANCE
        );
    }

    @Test
    public void testParseIp4FirstOctetIsInvalidValueFails() {
        this.parseIp4Fails(
            "987.2.3.4",
            false, // insideIp6
            HostAddressProblemInvalidValue.with(0)
        );
    }

    @Test
    public void testParseIp4SecondOctetIsInvalidValueFails() {
        this.parseIp4Fails(
            "1.987.3.4",
            false, // insideIp6
            HostAddressProblemInvalidValue.with(2)
        );
    }

    @Test
    public void testParseIp4ThirdOctetIsInvalidValueFails() {
        this.parseIp4Fails(
            "1.2.987.4",
            false, // insideIp6
            HostAddressProblemInvalidValue.with(4)
        );
    }

    @Test
    public void testParseIp4LastOctetIsInvalidValueFails() {
        this.parseIp4Fails(
            "1.2.3.987",
            false, // insideIp6
            HostAddressProblemInvalidValue.with(6)
        );
    }

    @Test
    public void testParseIp4Valid() {
        this.parseIp4AndVerify(
            "1.2.3.4",
            0x01020304,
            false // insideIp6
        );
    }

    @Test
    public void testParseIp4Valid2() {
        this.parseIp4AndVerify(
            "11.22.33.44",
            (11 << 24) | (22 << 16) | (33 << 8) | 44, // insideIp6
            false
        );
    }

    @Test
    public void testParseIp4ValidFirstOctetIsZero() {
        this.parseIp4AndVerify(
            "0.2.3.4",
            0x00020304,
            false // insideIp6
        );
    }

    @Test
    public void testParseIp4ValidSecondOctetIsZero() {
        this.parseIp4AndVerify(
            "1.0.3.4",
            0x01000304,
            false // insideIp6
        );
    }

    @Test
    public void testParseIp4ValidThirdOctetIsZero() {
        this.parseIp4AndVerify(
            "1.2.0.4",
            0x01020004,
            false // insideIp6
        );
    }

    @Test
    public void testParseIp4ValidLastOctetIsZero() {
        this.parseIp4AndVerify(
            "1.2.3.0",
            0x01020300,
            false // insideIp6
        );
    }

    @Test
    public void testParseIp4ValidFirstOctetIsManyZeros() {
        this.parseIp4AndVerify(
            "00000.2.3.4",
            0x00020304,
            false // insideIp6
        );
    }

    @Test
    public void testParseIp4ValidSecondOctetIsManyZeros() {
        this.parseIp4AndVerify(
            "1.00000.3.4",
            0x01000304,
            false // insideIp6
        );
    }

    @Test
    public void testParseIp4ValidThirdOctetIsManyZeros() {
        this.parseIp4AndVerify(
            "1.2.00000.4",
            0x01020004,
            false // insideIp6
        );
    }

    @Test
    public void testParseIp4ValidLastOctetIsManyZeros() {
        this.parseIp4AndVerify(
            "1.2.3.00000",
            0x01020300,
            false // insideIp6
        );
    }

    @Test
    public void testParseIp4ValidWithStartAndEnd() {
        final String address = "!1.2.3.4!";
        this.parseIp4AndVerify(
            address,
            1,
            address.length() - 1,
            false, // insideIp6
            0x01020304
        );
    }

    @Test
    public void testParseIp4OnlyOneOctet() {
        this.parseIp4Fails(
            "1",
            false, // insideIp6
            HostAddressProblemIncomplete.INSTANCE
        );
    }

    @Test
    public void testParseIp4TwoOctets() {
        this.parseIp4Fails(
            "1.2",
            false, // insideIp6
            HostAddressProblemIncomplete.INSTANCE
        );
    }

    @Test
    public void testParseIp4ThreeOctets() {
        this.parseIp4Fails(
            "1.2.3",
            false, // insideIp6
            HostAddressProblemIncomplete.INSTANCE
        );
    }

    @Test
    public void testParseIp4FiveOctets() {
        final String address = "1.2.3.4.5";
        this.parseIp4Fails(
            address,
            false, // insideIp6
            HostAddressProblemInvalidCharacter.with(
                address.indexOf('4') + 1
            )
        );
    }

    @Test
    public void testParseIp4TooLongOnlyOctet() {
        final char[] array = new char[HostAddress.MAX_LABEL_LENGTH];
        Arrays.fill(array, '0');
        final String name = new String(array);
        this.parseIp4Fails(
            name,
            false, // insideIp6
            HostAddressProblemInvalidLength.with(0)
        );
    }

    @Test
    public void testParseIp4TooLongFirstOctet() {
        final char[] array = new char[HostAddress.MAX_LABEL_LENGTH];
        Arrays.fill(array, '0');
        final String name = new String(array);
        this.parseIp4Fails(
            name + ".1",
            false,
            HostAddressProblemInvalidLength.with(0)
        );
    }

    @Test
    public void testParseIp4TooLongSecondOctet() {
        final char[] array = new char[HostAddress.MAX_LABEL_LENGTH];
        Arrays.fill(array, '0');
        this.parseIp4Fails(
            "1." + new String(array) + ".3.4",
            false, // insideIp6
            HostAddressProblemInvalidLength.with(2)
        );
    }

    @Test
    public void testParseIp4TooLongThirdOctet() {
        final char[] array = new char[HostAddress.MAX_LABEL_LENGTH];
        Arrays.fill(array, '0');
        this.parseIp4Fails("1.2." + new String(array) + ".4", false, HostAddressProblemInvalidLength.with(4));
    }

    @Test
    public void testParseIp4TooLongLastOctet() {
        final char[] array = new char[HostAddress.MAX_LABEL_LENGTH];
        Arrays.fill(array, '0');
        this.parseIp4Fails(
            "1.2.3." + new String(array),
            false, // insideIp6
            HostAddressProblemInvalidLength.with(6)
        );
    }

    // helpers
    private void parseIp4AndVerify(final String address,
                                   final long value,
                                   final boolean insideIp6) {
        this.parseIp4AndVerify(
            address,
            0,
            address.length(),
            insideIp6,
            value
        );
    }

    private void parseIp4AndVerify(final String address,
                                   final int start,
                                   final int end,
                                   final boolean insideIp6,
                                   final long value) {
        final Object result = HostAddress.tryParseIp4(
            address,
            start,
            end,
            insideIp6
        );
        if (result instanceof HostAddressProblem) {
            fail(((HostAddressProblem) result).message(address));
        }
        assertArrayEquals(HostAddress.toBytes(value), HostAddress.toBytes((Long) result));
    }

    private void parseIp4Fails(final String address,
                               final boolean insideIp6,
                               final HostAddressProblem problem) {
        this.parseIp4Fails(
            address,
            0,
            address.length(),
            insideIp6,
            problem
        );
    }

    private void parseIp4Fails(final String address,
                               final int start,
                               final int end,
                               final boolean insideIp6,
                               final HostAddressProblem problem) {
        this.checkEquals(
            problem,
            HostAddress.tryParseIp4(
                address,
                start,
                end,
                insideIp6
            ),
            "problem"
        );
    }

    // parseIp6 ........................................................................................................

    @Test
    public void testParseIp6OnlyColonFails() {
        this.parseIp6Fails(
            ":",
            HostAddressProblemIncomplete.INSTANCE
        );
    }

    @Test
    public void testParseIp6StartsWithColonFails() {
        this.parseIp6Fails(
            ":2:3:4:5:6:7:8",
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testParseIp6EndsWithColonFails() {
        this.parseIp6Fails(
            "1:2:3:4:5:6:7:",
            HostAddressProblemIncomplete.INSTANCE
        );
    }

    @Test
    public void testParseIp6FirstGroupStartsWithInvalidCharacterFails() {
        this.parseIp6Fails(
            "!1:2:3:4:5:6:7:8",
            HostAddressProblemInvalidCharacter.with(0)
        );
    }

    @Test
    public void testParseIp6FirstGroupIncludesInvalidCharacterFails() {
        this.parseIp6Fails(
            "1!:2:3:4:5:6:7:8",
            HostAddressProblemInvalidCharacter.with(1)
        );
    }

    @Test
    public void testParseIp6FirstGroupIncludesInvalidValueFails() {
        this.parseIp6Fails(
            "12345:2:3:4:5:6:7:8",
            HostAddressProblemInvalidValue.with(0)
        );
    }

    @Test
    public void testParseIp6FirstGroupIncludesInvalidValueTooManyZeroesFails() {
        this.parseIp6Fails(
            "00000:2:3:4:5:6:7:8",
            HostAddressProblemInvalidValue.with(0)
        );
    }

    @Test
    public void testParseIp6SecondGroupStartsWithInvalidCharacterFails() {
        this.parseIp6Fails(
            "1:!2:3:4:5:6:7:8",
            HostAddressProblemInvalidCharacter.with(2)
        );
    }

    @Test
    public void testParseIp6SecondGroupIncludesInvalidCharacterFails() {
        this.parseIp6Fails(
            "1:2!:3:4:5:6:7:8",
            HostAddressProblemInvalidCharacter.with(3)
        );
    }

    @Test
    public void testParseIp6SecondGroupIncludesInvalidValueFails() {
        this.parseIp6Fails(
            "1:23456:3:4:5:6:7:8",
            HostAddressProblemInvalidValue.with(2)
        );
    }

    @Test
    public void testParseIp6SecondGroupIncludesInvalidValueTooManyZeroesFails() {
        this.parseIp6Fails(
            "1:00000:3:4:5:6:7:8",
            HostAddressProblemInvalidValue.with(2)
        );
    }

    @Test
    public void testParseIp6LastGroupStartsWithInvalidCharacterFails() {
        this.parseIp6Fails(
            "1:2:3:4:5:6:7:!",
            HostAddressProblemInvalidCharacter.with(14)
        );
    }

    @Test
    public void testParseIp6LastGroupIncludesInvalidCharacterFails() {
        this.parseIp6Fails(
            "1:2:3:4:5:6:7:8!",
            HostAddressProblemInvalidCharacter.with(14 + 1)
        );
    }

    @Test
    public void testParseIp6LastGroupIncludesInvalidValueFails() {
        this.parseIp6Fails(
            "1:2:3:4:5:6:7:89ABC",
            HostAddressProblemInvalidValue.with(14)
        );
    }

    @Test
    public void testParseIp6LastGroupIncludesInvalidValueTooManyZeroesFails() {
        this.parseIp6Fails(
            "1:2:3:4:5:6:7:00000",
            HostAddressProblemInvalidValue.with(14)
        );
    }

    @Test
    public void testParseIp6TooManyGroupsFails() {
        final String address = "1:2:3:4:5:6:7:8:9";
        this.parseIp6Fails(
            address,
            HostAddressProblemInvalidCharacter.with(
                address.indexOf('9') - 1)
        );
    }

    @Test
    public void testParseIp6WithoutEmpty() {
        this.parseIp6AndCheck(
            "1:2:3:4:5:6:7:8",
            "00010002000300040005000600070008"
        );
    }

    @Test
    public void testParseIp6WithoutEmpty2() {
        this.parseIp6AndCheck(
            "1122:3344:5566:7788:99aa:bbcc:ddee:ff01",
            "112233445566778899aabbccddeeff01"
        );
    }

    @Test
    public void testParseIp6WithoutEmptyAndStartAndEnd() {
        final String address = "!1:2:3:4:5:6:7:8!";
        this.parseIp6AndCheck(
            address,
            1,
            address.length() - 1,
            "00010002000300040005000600070008"
        );
    }

    @Test
    public void testParseIp6MoreThanOneEmptyFails() {
        final String address = "1::3:4::6:7:8";
        this.parseIp6Fails(
            address,
            HostAddressProblemInvalidCharacter.with(
                address.indexOf('6') - 1
            )
        );
    }

    @Test
    public void testParseIp6StartsWithEmpty() {
        this.parseIp6AndCheck(
            "::2:3:4:5:6:7:8",
            "00000002000300040005000600070008"
        );
    }

    @Test
    public void testParseIp6StartsWithEmpty2() {
        this.parseIp6AndCheck(
            "::3:4:5:6:7:8",
            "00000000000300040005000600070008"
        );
    }

    @Test
    public void testParseIp6StartsWithEmpty3() {
        this.parseIp6AndCheck(
            "::4:5:6:7:8",
            "00000000000000040005000600070008"
        );
    }

    @Test
    public void testParseIp6StartsWithEmpty4() {
        this.parseIp6AndCheck(
            "::8",
            "00000000000000000000000000000008"
        );
    }

    @Test
    public void testParseIp6MiddleEmpty() {
        this.parseIp6AndCheck(
            "1:2:3::5:6:7:8",
            "00010002000300000005000600070008"
        );
    }

    @Test
    public void testParseIp6MiddleEmpty2() {
        this.parseIp6AndCheck(
            "1:2:3::6:7:8",
            "00010002000300000000000600070008"
        );
    }

    @Test
    public void testParseIp6MiddleEmpty3() {
        this.parseIp6AndCheck(
            "1:2:3::7:8",
            "00010002000300000000000000070008"
        );
    }

    @Test
    public void testParseIp6EndsWithEmpty() {
        this.parseIp6AndCheck(
            "1:2:3:4:5:6:7::",
            "00010002000300040005000600070000"
        );
    }

    @Test
    public void testParseIp6EndsWithEmpty2() {
        this.parseIp6AndCheck(
            "1:2:3:4:5:6::",
            "00010002000300040005000600000000"
        );
    }

    @Test
    public void testParseIp6EndsWithEmpty3() {
        this.parseIp6AndCheck(
            "1:2:3:4:5::",
            "00010002000300040005000000000000"
        );
    }

    @Test
    public void testParseIp6EndsWithEmpty4() {
        this.parseIp6AndCheck(
            "1::",
            "00010000000000000000000000000000"
        );
    }

    @Test
    public void testParseIp6OnlyEmpty() {
        this.parseIp6AndCheck(
            "::",
            "00000000000000000000000000000000"
        );
    }

    @Test
    public void testParseIp6Ip4WithInvalidValueFails() {
        final String address = "1:2:3:4:5:6:7.8.900.1";
        this.parseIp6Fails(
            address,
            HostAddressProblemInvalidValue.with(
                address.indexOf('9')
            )
        );
    }

    @Test
    public void testParseIp6Ip4WithColonFails() {
        final String address = "1:2:3:4:5:6:7.8:9.0";
        this.parseIp6Fails(
            address,
            HostAddressProblemInvalidCharacter.with(
                address.indexOf('8') + 1
            )
        );
    }

    @Test
    public void testParseIp6Ip4BeforeTooFewGroupsFails() {
        final String address = "1:2:3.4.5.6";
        this.parseIp6Fails(
            address,
            HostAddressProblemInvalidCharacter.with(
                address.indexOf('.')
            )
        );
    }

    @Test
    public void testParseIp6Ip4BeforeTooManyGroupsFails() {
        final String address = "1:2:3:4:5:6:7:1.2.3.4";
        this.parseIp6Fails(
            address,
            HostAddressProblemInvalidCharacter.with(
                address.indexOf('.')
            )
        );
    }

    @Test
    public void testParseIp6Ip4StartsWithEmpty() {
        this.parseIp6AndCheck(
            "::1:2:3:4:5:6.7.8.9",
            "00000001000200030004000506070809"
        );
    }

    @Test
    public void testParseIp6Ip4StartsWithEmpty2() {
        this.parseIp6AndCheck(
            "::1:2:3:5.6.7.8",
            "00000000000000010002000305060708"
        );
    }

    @Test
    public void testParseIp6Ip4StartsWithEmpty3() {
        this.parseIp6AndCheck(
            "::1:2:5.6.7.8",
            "00000000000000000001000205060708"
        );
    }

    @Test
    public void testParseIp6Ip4StartsWithEmpty4() {
        this.parseIp6AndCheck(
            "::1:5.6.7.8",
            "00000000000000000000000105060708"
        );
    }

    @Test
    public void testParseIp6Ip4StartsWithOnlyEmpty() {
        this.parseIp6AndCheck(
            "::5.6.7.8",
            "00000000000000000000000005060708"
        );
    }

    @Test
    public void testParseIp6Ip4WithMiddleEmpty1() {
        this.parseIp6AndCheck(
            "1:2:3:4:5::255.1.2.3",
            "000100020003000400050000ff010203"
        );
    }

    @Test
    public void testParseIp6Ip4WithMiddleEmpty2() {
        this.parseIp6AndCheck(
            "1:2:3:4::255.1.2.3",
            "000100020003000400000000ff010203"
        );
    }

    @Test
    public void testParseIp6Ip4WithMiddleEmpty3() {
        this.parseIp6AndCheck(
            "1::255.1.2.3",
            "000100000000000000000000ff010203"
        );
    }

    @Test
    public void testParseIp6Ip4WithoutAnyPreceedingBlocks() {
        this.parseIp6AndCheck(
            "::255.1.2.3",
            "000000000000000000000000ff010203"
        );
    }

    @Test
    public void testParseIp6Ip4WithoutEmpty() {
        final String address = "1:2:3:4:5:6:255.7.8.9";
        this.parseIp6AndCheck(
            address,
            "000100020003000400050006ff070809"
        );
    }

    @Test
    public void testParseIp6WithStartAndEnd() {
        final String address = "!1:2:3:4:5:6:7:8!";
        this.parseIp6AndCheck(
            address,
            1,
            address.length() - 1,
            "00010002000300040005000600070008"
        );
    }

    // helpers

    private void parseIp6AndCheck(final String address,
                                  final String value) {
        this.parseIp6AndCheck(
            address,
            toByteArray(value)
        );
    }

    private void parseIp6AndCheck(final String address,
                                  final byte[] value) {
        this.parseIp6AndCheck(
            address,
            0,
            address.length(),
            value
        );
    }

    private void parseIp6AndCheck(final String address,
                                  final int start,
                                  final int end,
                                  final String value) {
        this.parseIp6AndCheck(
            address,
            start,
            end,
            toByteArray(value)
        );
    }

    private void parseIp6AndCheck(final String address,
                                  final int start,
                                  final int end,
                                  final byte[] value) {
        final Object result = HostAddress.tryParseIp6(address, start, end);
        if (result instanceof HostAddressProblem) {
            this.checkEquals(value,
                ((HostAddressProblem) result).message(address),
                "failed " + CharSequences.quote(address));
        }
        checkEquals("bytes[]", value, (byte[]) result);
    }

    private void parseIp6Fails(final String address,
                               final HostAddressProblem problem) {
        this.parseIp6Fails(
            address,
            0,
            address.length(),
            problem
        );
    }

    private void parseIp6Fails(final String address,
                               final int start,
                               final int end,
                               final HostAddressProblem problem) {
        final Object result = HostAddress.tryParseIp6(address, start, end);
        if (result instanceof byte[]) {
            fail("Should have failed=" + toHexString((byte[]) result));
        }
        final HostAddressProblem actual = (HostAddressProblem) result;
        if (false == problem.equals(actual)) {
            this.checkEquals(problem.message(address),
                actual.message(address),
                "wrong problem returned");
        }
    }

    /**
     * Parses the {@link String} of hex values assuming that it has hex digits in big endian form.
     */
    private byte[] toByteArray(final String hexDigits) {
        this.checkEquals(HostAddress.IP6_OCTET_COUNT * 2,
            hexDigits.length(),
            "hexValues string has wrong number of characters=" + hexDigits);
        return CharSequences.bigEndianHexDigits(hexDigits);
    }

    // Comparable.......................................................................................................

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(
            HostAddress.with("zebra.com")
        );
    }

    @Test
    public void testCompareLessCaseInsignificant() {
        this.compareToAndCheckLess(
            HostAddress.with("ZEBRA.com")
        );
    }

    @Test
    public void testCompareEqualsCaseInsignificant() {
        this.compareToAndCheckEquals(
            HostAddress.with(HOST.toUpperCase())
        );
    }

    @Override
    public HostAddress createComparable() {
        return this.createObject();
    }

    @Override
    public HostAddress createObject() {
        return HostAddress.with(HOST.toLowerCase());
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final String address = "address";
        this.toStringAndCheck(HostAddress.with(address), address);
    }

    // class............................................................................................................

    @Override
    public Class<HostAddress> type() {
        return HostAddress.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // helpers..........................................................................................................

    public void checkEquals(final String message,
                            final byte[] expected,
                            final byte[] actual) {
        if (false == Arrays.equals(expected, actual)) {
            this.checkEquals(toHexString(expected), toHexString(actual), message);
        }
    }

    /**
     * Accepts some bytes in big endian form and builds a hex string representation.
     */
    static private String toHexString(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (final byte value : bytes) {
            builder.append(
                CharSequences.padLeft(
                    Integer.toHexString(0xff & value),
                    2,
                    '0'
                )
            );
        }
        return builder.toString();
    }

}
