/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at legal-notices/CDDLv1_0.txt
 * or http://forgerock.org/license/CDDLv1.0.html.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at legal-notices/CDDLv1_0.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Copyright 2008 Sun Microsystems, Inc.
 *      Portions Copyright 2010-2015 ForgeRock AS.
 *      Portions Copyright 2012 Dariusz Janny <dariusz.janny@gmail.com>
 */
package org.opends.server.extensions;

import org.forgerock.opendj.ldap.ByteString;
import org.opends.server.TestCaseUtils;
import org.opends.server.admin.server.AdminTestCaseUtils;
import org.opends.server.admin.std.meta.CryptPasswordStorageSchemeCfgDefn;
import org.opends.server.admin.std.server.CryptPasswordStorageSchemeCfg;
import org.opends.server.types.Entry;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.opends.server.extensions.PasswordStorageSchemeTestCase.*;


/**
 * A set of test cases for the crypt password storage scheme.
 */
@SuppressWarnings("javadoc")
public class CryptPasswordStorageSchemeTestCase
       extends ExtensionsTestCase
{
  /** Names of all the crypt algorithms we want to test. */
  private static final String[] names = { "unix", "md5", "sha256", "sha512" };

  /**
   * Creates a new instance of this crypt password storage scheme test
   * case with the provided information.
   */
  public CryptPasswordStorageSchemeTestCase()
  {
    super();
  }


  /**
   * Ensures that the Directory Server is started before running any of these
   * tests.
   */
  @BeforeClass
  public void startServer() throws Exception
  {
    TestCaseUtils.startServer();
  }



  /**
   * Retrieves a set of passwords that may be used to test the password storage
   * scheme.
   *
   * @return  A set of passwords that may be used to test the password storage
   *          scheme.
   */
  @DataProvider(name = "testPasswords")
  public Object[][] getTestPasswords()
  {
    return getTestPasswordsStatic();
  }


  /**
   * Creates an instance of each password storage scheme, uses it to encode the
   * provided password, and ensures that the encoded value is correct.
   *
   * @param  plaintext  The plain-text version of the password to encode.
   * @throws  Exception  If an unexpected problem occurs.
   */
  @Test(dataProvider = "testPasswords")
  public void testUnixStorageSchemes(ByteString plaintext)
         throws Exception
  {
    for (String name : names)
    {
      testStorageScheme(plaintext, getScheme(name));
    }
  }


  @DataProvider
  public Object[][] passwordsForBinding()
  {
    return PasswordStorageSchemeTestCase.passwordsForBinding();
  }



  /**
   * An end-to-end test that verifies that we can set a pre-encoded password
   * in a user entry, and then bind as that user using the cleartext password.
   */
  @Test(dataProvider = "passwordsForBinding")
  public void testSettingUnixEncodedPassword(ByteString plainPassword)
          throws Exception
  {
    for (String name: names)
    {
      testSettingEncodedPassword(plainPassword, getScheme(name));
    }
  }

  /**
   * Retrieves a set of passwords (plain and variously hashed) that may
   * be used to test the different Unix "crypt" algorithms used by the Crypt
   * Password Storage scheme.
   *
   * The encrypted versions have been generated by the openssl passwd -1
   * command on MacOS X.
   *
   * @return  A set of couple (cleartext, hashed) passwords that
   *          may be used to test the different algorithms used by the Crypt
   *          password storage scheme.
   */
  @DataProvider(name = "testCryptPasswords")
  public Object[][] getTestCryptPasswords()
         throws Exception
  {
    return new Object[][]
    {
      new Object[] { "secret12", "{CRYPT}$1$X40CcMaA$dd3ndknBLcpkED4/RciyD1" },
      new Object[] { "#1 Strong Password!", "{CRYPT}$1$7jHbWKyy$gAmpOSdaYVap55MwsQnK5/" },
      new Object[] { "foo", "{CRYPT}$1$ac/Z7Q3s$5kTVLqMSq9KMqUVyEBfiw0" },
      new Object[] { "secret12", "{CRYPT}$5$miWe9yahchas7aiy$b/6oTh5QF3bqbdIDWmjtdOxD8df75426zTHwF.MJuyB" },
      new Object[] { "foo", "{CRYPT}$5$aZoothaeDai0nooG$5LDMuhK6gWtH6/mrrqZbRc5aIRROfrKri4Tvl/D6Z.0"},
      new Object[] { "#1 Strong Password!", "{CRYPT}$5$aZoothaeDai0nooG$6o0Sbx/RtTA4K/A8uflMsSCid3i7TYktcwWxIp5NFy2"},
      new Object[] { "secret12", "{CRYPT}$6$miWe9yahchas7aiy$RQASn5qZMCu2FDsR69RHk1RoLVi3skFUhS0qGNCo.MymgkYoWAedMji09UzxMFzOj8fW2GnzsXT4RVn9gcNmf0" },
      new Object[] { "#1 Strong Password!", "{CRYPT}$6$p0NJY6r4$VV2JfNtRaTmy8hBtVpdgeIUYQIAUyfdLyhiH6VxzsDIw.28oCsVeMQ5ARiL/PoOambM9dAU3vk4ll8uEB/nnx0"},
      new Object[] { "foo", "{CRYPT}$6$aZoothaeDai0nooG$1K9ePro8ujsqRy/Ag77OVuev8Y8hyN1Jp10S2t9S.1RMtkKn/SbxQbl2MezoL0UJFYjrEzL0zVdO8PcfT3yXS."}
    };
  }

  @Test(dataProvider = "testCryptPasswords")
  public void testAuthCryptPasswords(
          String plaintextPassword,
          String encodedPassword) throws Exception
  {
    testAuthPasswords("TestCrypt", plaintextPassword, encodedPassword);
  }

  /**
   * Retrieves an initialized instance of this password storage scheme.
   *
   * @return  An initialized instance of this password storage scheme.
   *
   * @throws  Exception  If an unexpected problem occurs.
   */
  private CryptPasswordStorageScheme getScheme(String algo)
         throws Exception
  {
    CryptPasswordStorageScheme scheme =
         new CryptPasswordStorageScheme();
    Entry e = TestCaseUtils.makeEntry(
      "dn: cn=CRYPT,cn=Password Storage Schemes,cn=config",
      "objectClass: top",
      "objectClass: ds-cfg-password-storage-scheme",
      "objectClass: ds-cfg-crypt-password-storage-scheme",
      "cn: CRYPT",
      "ds-cfg-java-class: org.opends.server.extensions.CryptPasswordStorageScheme",
      "ds-cfg-enabled: true",
      "ds-cfg-crypt-password-storage-encryption-algrithm: " + algo
);
    CryptPasswordStorageSchemeCfg configuration =
         AdminTestCaseUtils.getConfiguration(
              CryptPasswordStorageSchemeCfgDefn.getInstance(),
              e);

    scheme.initializePasswordStorageScheme(configuration);
    return scheme;
  }
}
