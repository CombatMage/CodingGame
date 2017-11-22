"""
helper to merge multiple kotlin src files into a single file
"""

from os import listdir
from os.path import isfile, join


MERGED_FILE_NAME = "Merged.kt"


def get_kotlin_files(path):
    """
    scans given dir for kotlin source code (.kt)
    returns list of files
    """
    kotlin_files = [
        join(path, f)
        for f in listdir(path) if isfile(join(path, f)) and f.endswith(".kt") and "Test" not in f
    ]
    return kotlin_files


def read_file_by_line(file_path):
    """
    reads content of file and return list of lines
    """
    lines = []
    with open(file_path, "r") as file:
        lines = file.readlines()
    return lines


def merge_imports(imports):
    """
    merge imports by removing all duplicates
    >>> merge_imports(["import com.example.a", "import com.example.a"])
    ['import com.example.a']
    """
    return list(set(imports))


def merge_kotlin_files(path):
    """
    helper to merge multiple kotlin src files into a single file
    """
    files = get_kotlin_files(path)
    content = [read_file_by_line(f) for f in files]

    all_imports = []
    all_code = []

    for src_code in content:
        all_imports += [line for line in src_code if line.startswith("import")]
        all_code += [line for line in src_code if not line.startswith("import")]

    all_imports = merge_imports(all_imports)

    merged_src_code = all_imports + ["\n", "\n"] + all_code
    with open(MERGED_FILE_NAME, "w") as file:
        file.writelines(merged_src_code)


if __name__ == "__main__":
    import doctest
    doctest.testmod()

    merge_kotlin_files("src")
