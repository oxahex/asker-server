package oxahex.asker.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TokenDto {

  private final String accessToken;
  private final String refreshToken;
}
