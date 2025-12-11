package cl.project.miarcapet.service.impl;

import cl.project.miarcapet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación personalizada del UserDetailsService de Spring Security.
 * Usado por Spring Security para cargar datos específicos del usuario durante la autenticación.
 *
 * Este servicio es llamado por AuthenticationManager cuando autentica usuarios.
 * Carga la entidad User desde la base de datos y la retorna como UserDetails.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga un usuario por su username (email en nuestro caso).
     * Llamado por Spring Security durante el proceso de autenticación.
     *
     * @param username La dirección de email del usuario
     * @return Objeto UserDetails (nuestra entidad User implementa esta interfaz)
     * @throws UsernameNotFoundException si el usuario no se encuentra
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + username
                ));
    }
}
