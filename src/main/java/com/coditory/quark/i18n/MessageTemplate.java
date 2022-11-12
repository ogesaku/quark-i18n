package com.coditory.quark.i18n;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

class MessageTemplate {
    private final String template;
    private final List<MessageTemplateNode> templateNodes;

    MessageTemplate(String template, List<MessageTemplateNode> templateNodes) {
        this.template = requireNonNull(template);
        this.templateNodes = requireNonNull(templateNodes);
    }

    public String format(Object[] args) {
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