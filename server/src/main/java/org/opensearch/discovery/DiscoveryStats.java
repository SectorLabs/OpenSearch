/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.discovery;

import org.opensearch.LegacyESVersion;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.common.io.stream.Writeable;
import org.opensearch.common.xcontent.ToXContentFragment;
import org.opensearch.common.xcontent.XContentBuilder;
import org.opensearch.cluster.coordination.PendingClusterStateStats;
import org.opensearch.cluster.coordination.PublishClusterStateStats;

import java.io.IOException;

public class DiscoveryStats implements Writeable, ToXContentFragment {

    private final PendingClusterStateStats queueStats;
    private final PublishClusterStateStats publishStats;

    public DiscoveryStats(PendingClusterStateStats queueStats, PublishClusterStateStats publishStats) {
        this.queueStats = queueStats;
        this.publishStats = publishStats;
    }

    public DiscoveryStats(StreamInput in) throws IOException {
        queueStats = in.readOptionalWriteable(PendingClusterStateStats::new);

        if (in.getVersion().onOrAfter(LegacyESVersion.V_6_1_0)) {
            publishStats = in.readOptionalWriteable(PublishClusterStateStats::new);
        } else {
            publishStats = null;
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeOptionalWriteable(queueStats);

        if (out.getVersion().onOrAfter(LegacyESVersion.V_6_1_0)) {
            out.writeOptionalWriteable(publishStats);
        }
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject(Fields.DISCOVERY);
        if (queueStats != null) {
            queueStats.toXContent(builder, params);
        }
        if (publishStats != null) {
            publishStats.toXContent(builder, params);
        }
        builder.endObject();
        return builder;
    }

    static final class Fields {
        static final String DISCOVERY = "discovery";
    }

    public PendingClusterStateStats getQueueStats() {
        return queueStats;
    }

    public PublishClusterStateStats getPublishStats() {
        return publishStats;
    }
}
