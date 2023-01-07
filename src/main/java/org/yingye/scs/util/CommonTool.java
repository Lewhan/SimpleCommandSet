package org.yingye.scs.util;

import org.yingye.scs.core.Config;

import java.util.Map;

public class CommonTool {

    public static String mapToJsonString(Map<?, ?> origin, Object parentKey, int deep) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat((deep - 1) * Config.TAB_SIZE));
        if (parentKey != null) {
            sb.append(parentKey).append(": ");
        }
        sb.append("{").append("\n");

        for (Object key : origin.keySet()) {
            Object data = origin.get(key);
            if (data instanceof Map) {
                sb.append(mapToJsonString((Map<?, ?>) data, key, deep + 1));
            } else if (!key.equals("==")) {
                sb.append(" ".repeat(deep * Config.TAB_SIZE))
                        .append(key)
                        .append(": ")
                        .append(data)
                        .append("\n");
            }
        }

        sb.append(" ".repeat((deep - 1) * Config.TAB_SIZE))
                .append("}")
                .append("\n");
        return sb.toString();
    }

}
