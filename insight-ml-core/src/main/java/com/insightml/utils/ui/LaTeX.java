/*
 * Copyright (C) 2016 Stefan Henß
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.insightml.utils.ui;

import java.util.List;

import com.google.common.base.Joiner;
import com.insightml.utils.Strings;

public final class LaTeX {

    private LaTeX() {
    }

    private static String escape(final String str) {
        return str.replace("{", "\\{").replace("}", "\\}")
                .replaceAll("([^ ]+)\\^\\((.+?)\\)", "\\$$1^{$2}\\$");
    }

    public static String table(final String caption, final String[] columns,
            final List<Object[]> cells, final boolean fullWidth, final double nameWidth,
            final int hlineFreq) {
        final StringBuilder builder = new StringBuilder(1024);
        builder.append("\\begin{table" + (fullWidth ? "*" : "") + "}[h!]\n");
        builder.append("\\centering\n\\small\n\\newcolumntype{P}[1]{>{\\centering\\arraybackslash}p{#1}}\n\\newcolumntype{C}{>{\\centering\\arraybackslash}X}%\n");
        builder.append("\\begin{tabular" + (fullWidth ? "x}{\\textwidth}" : "}") + "{l|p{"
                + nameWidth + "cm}"
                + Strings.repeat("|" + (fullWidth ? "C" : "c"), columns.length - 1)
                + "}\\hline\n\\bf\\footnotesize \\# & \\bf\\footnotesize ");
        builder.append(Joiner.on(" & \\bf\\footnotesize ").join(columns).replace("MA-", "")
                .replaceAll("(MI)-([\\S]+)", "\\$\\\\bf $2_{$1}\\$")
                .replace("Selections", "\\#Sel.").replace("CosSim{Tf}", "TF")
                .replace("CosSim{Tf*Idf}", "TfIdf").replaceAll("ROUGE-([0-9])", "RO-$1")
                .replaceAll("BLEU-([0-9])", "BL-$1"));
        builder.append("\\\\\n\\hline\\hline\n");
        int i = 0;
        for (final Object[] cell : cells) {
            builder.append(++i);
            for (final Object element : cell) {
                builder.append(" & ");
                if (element instanceof String) {
                    builder.append(escape((String) element));
                } else if (element instanceof Number) {
                    builder.append(new SimpleFormatter(4, true).format(((Number) element)
                            .doubleValue()));
                } else {
                    builder.append(element);
                }
            }
            builder.append("\\\\\n");
            if (i % hlineFreq == 0) {
                builder.append("\\hline\n");
            }
        }
        builder.append("\\hline\\end{tabular" + (fullWidth ? "x" : "") + "}\n\\caption{"
                + escape(caption) + "}\n\\label{table:"
                + caption.replace(' ', '-').replaceAll("[}{,]+", "").toLowerCase()
                + "}\n\\end{table" + (fullWidth ? "*" : "") + "}\n");
        return builder.toString();
    }

}
