package com.eldersphere.core.config;

import com.eldersphere.core.context.RequestContext;
import com.eldersphere.core.entities.User;
import com.eldersphere.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAwareImpl")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<User> {

    private final RequestContext requestContext;
    private final UserRepository userRepository;

    @Override
    public Optional<User> getCurrentAuditor() {
        if (!requestContext.hasUser()) {
            return Optional.empty();
        }
        // getReferenceById returns a proxy — no extra DB query
        return Optional.of(userRepository.getReferenceById(requestContext.getCurrentUserId()));
    }
}
