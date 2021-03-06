/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.composite.internal;

import org.gradle.api.artifacts.component.BuildIdentifier;
import org.gradle.includedbuild.internal.IncludedBuildTaskGraph;
import org.gradle.internal.component.model.ComponentArtifactMetadata;

import java.util.Set;

class IncludedBuildArtifactBuilder {
    private final IncludedBuildTaskGraph includedBuildTaskGraph;

    IncludedBuildArtifactBuilder(IncludedBuildTaskGraph includedBuildTaskGraph) {
        this.includedBuildTaskGraph = includedBuildTaskGraph;
    }

    public void build(BuildIdentifier requestingBuild, ComponentArtifactMetadata artifact) {
        if (artifact instanceof CompositeProjectComponentArtifactMetadata) {
            CompositeProjectComponentArtifactMetadata compositeBuildArtifact = (CompositeProjectComponentArtifactMetadata) artifact;
            BuildIdentifier targetBuild = getBuildIdentifier(compositeBuildArtifact);
            Set<String> tasks = compositeBuildArtifact.getTasks();
            for (String taskName : tasks) {
                includedBuildTaskGraph.addTask(requestingBuild, targetBuild, taskName);
            }
            for (String taskName : tasks) {
                includedBuildTaskGraph.awaitCompletion(targetBuild, taskName);
            }
        }
    }

    private BuildIdentifier getBuildIdentifier(CompositeProjectComponentArtifactMetadata artifact) {
        return artifact.getComponentId().getBuild();
    }
}
