package de.espend.idea.laravel.stub;

import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.PhpFile;
import de.espend.idea.laravel.translation.utils.TranslationUtil;
import de.espend.idea.laravel.util.ArrayReturnPsiRecursiveVisitor;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class TranslationKeyStubIndex extends FileBasedIndexExtension<String, Void> {

    public static final ID<String, Void> KEY = ID.create("de.espend.idea.laravel.translation_keys");
    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return fileContent -> {

            final Map<String, Void> map = new THashMap<>();

            PsiFile psiFile = fileContent.getPsiFile();
            if(!(psiFile instanceof PhpFile)) {
                return map;
            }

            String namespace = TranslationUtil.getNamespaceFromFilePath(fileContent.getFile().getPath());
            if(namespace == null) {
                return map;
            }

            psiFile.acceptChildren(new ArrayReturnPsiRecursiveVisitor(
                namespace, (key, psiKey, isRootElement) -> map.put(key, null))
            );

            return map;
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return this.myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<Void> getValueExternalizer() {
        return ScalarIndexExtension.VOID_DATA_EXTERNALIZER;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getFileType() == PhpFileType.INSTANCE;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 2;
    }
}
