/**
 * Copyright 2012 Eric Wendelin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.eriwen.gradle.js.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.google.javascript.jscomp.CompilerOptions
import com.eriwen.gradle.js.JsMinifier
import org.codehaus.groovy.runtime.GStringImpl

class MinifyJsTask extends DefaultTask {
    private static final JsMinifier MINIFIER = new JsMinifier()

    // FIXME: Wire defaults in properly through convention (#14)
    CompilerOptions compilerOptions = new CompilerOptions()
    String compilationLevel = 'SIMPLE_OPTIMIZATIONS'
    String warningLevel = 'DEFAULT'
    def source
    def dest

	@TaskAction
	def run() {
        if (!source) {
            logger.warn('The syntax "inputs.files ..." is deprecated! Please use `source = "path1"`')
            logger.warn('This will be removed in the next version of the JS plugin')
            source = getInputs().files.files.collect { it.canonicalPath }
        } else if (source instanceof GStringImpl || source instanceof String) {
            source = [source]
        }

        if (!dest) {
            logger.warn('The syntax "outputs.files ..." is deprecated! Please use `dest = "dest/filename.js"`')
            dest = getOutputs().files.files.collect { it.canonicalPath }
        } else if (dest instanceof GStringImpl || dest instanceof String) {
            dest = [dest]
        }

        if (dest.size() == source.size()) {
            for (int i = 0; i < source.size(); i++) {
                MINIFIER.minifyJsFile(project.file(source[i]), project.file(dest[i]), compilerOptions, warningLevel, compilationLevel)
            }
        } else {
            throw new IllegalArgumentException("Could not map input files to output files. Found ${source.size()} inputs and ${dest.size()} outputs")
        }
	}
}
