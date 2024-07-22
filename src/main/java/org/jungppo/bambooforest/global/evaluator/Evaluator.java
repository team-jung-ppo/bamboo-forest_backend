package org.jungppo.bambooforest.global.evaluator;

public interface Evaluator<T> {
    boolean isEligible(T targetId, Long memberId);
}
