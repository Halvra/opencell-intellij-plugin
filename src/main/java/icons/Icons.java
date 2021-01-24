package icons;

import com.intellij.openapi.util.IconLoader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Icons {
    public static final Icon OPENCELL = IconLoader.getIcon("/icons/opencell.png", Icons.class);
}
