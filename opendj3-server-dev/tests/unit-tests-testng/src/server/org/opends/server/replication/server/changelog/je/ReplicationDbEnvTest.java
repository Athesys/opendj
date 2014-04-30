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
 *      Copyright 2014 ForgeRock AS
 */
package org.opends.server.replication.server.changelog.je;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opends.server.DirectoryServerTestCase;
import org.opends.server.TestCaseUtils;
import org.opends.server.replication.server.ChangelogState;
import org.opends.server.replication.server.changelog.api.ChangelogException;
import org.opends.server.types.DN;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.sleepycat.je.Database;
import com.sleepycat.je.Environment;

import static java.util.Arrays.*;

import static org.assertj.core.api.Assertions.*;
import static org.opends.server.replication.server.changelog.je.ReplicationDbEnv.*;

@SuppressWarnings("javadoc")
public class ReplicationDbEnvTest extends DirectoryServerTestCase
{

	/**
	 * Bypass heavyweight setup.
	 */
	private final class TestableReplicationDbEnv extends ReplicationDbEnv
	{
		private TestableReplicationDbEnv() throws ChangelogException
		{
			super(null, null);
		}

		@Override
		protected Environment openJEEnvironment(String path)
		{
			return null;
		}

		@Override
		protected Database openDatabase(String databaseName)
				throws ChangelogException, RuntimeException
		{
			return null;
		}
	}

	@BeforeClass
	public void setup() throws Exception
	{
		TestCaseUtils.startFakeServer();
	}

	@AfterClass
	public void teardown()
	{
		TestCaseUtils.shutdownFakeServer();
	}

	@DataProvider
	public Object[][] changelogStateDataProvider() throws Exception
	{
		return new Object[][] {
			{ DN.valueOf("dc=example,dc=com"), 524157415, asList(42, 346) },
			// test with a space in the baseDN (space is the field separator in the DB)
			{ DN.valueOf("cn=admin data"), 524157415, asList(42, 346) },
	  };
	}

	@Test(dataProvider = "changelogStateDataProvider")
	public void encodeDecodeChangelogState(DN baseDN, long generationId,
			List<Integer> serverIds) throws Exception
	{
		final ReplicationDbEnv changelogStateDB = new TestableReplicationDbEnv();

		// encode data
		final Map<byte[], byte[]> wholeState = new LinkedHashMap<byte[], byte[]>();
		put(wholeState, changelogStateDB.toGenIdEntry(baseDN, generationId));
		for (Integer serverId : serverIds)
		{
			put(wholeState, changelogStateDB.toReplicaEntry(baseDN, serverId));
		}

		// decode data
		final ChangelogState state =
				changelogStateDB.decodeChangelogState(wholeState);
		assertThat(state.getDomainToGenerationId()).containsExactly(
				entry(baseDN, generationId));
		assertThat(state.getDomainToServerIds()).containsExactly(
				entry(baseDN, serverIds));
	}

	private void put(Map<byte[], byte[]> map, Entry<String, String> entry)
	{
		map.put(toBytes(entry.getKey()), toBytes(entry.getValue()));
	}

}
