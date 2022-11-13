package com.coditory.quark.i18n;

import java.util.List;
import java.util.Objects;

import static com.coditory.quark.i18n.Preconditions.expectNonNull;
import static java.util.stream.Collectors.joining;

final class MessageTemplate {
    private final String template;
    private final List<MessageTemplateNode> templateNodes;

    MessageTemplate(String template, List<MessageTemplateNode> templateNodes) {
        expectNonNull(template, "template");
        expectNonNull(templateNodes, "templateNodes");
        this.template = template;
        this.templateNodes = List.copyOf(templateNodes);
    }

    public String format(Object[] args) {
        expectNonNull(args, "args");
        return templateNodes.stream()
                .map(node -> node.resolve(args))
                .collect(joining());
    }

    @Override
    public String toString() {
        return "MessageTemplate{" + template + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageTemplate that = (MessageTemplate) o;
        return template.equals(that.template);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template);
    }
}