{-

Depends
* The Haskell Platform http://hackage.haskell.org/platform/
* Lastik http://hackage.haskell.org/package/Lastik

-}
module Build where

import System.Build
import qualified System.Build.Java.Javac as J
import qualified System.Build.Java.Javadoc as D
import System.Cmd
import System.Directory
import System.Exit
import System.FilePath
import Control.Arrow
import Control.Monad
import Codec.Archive.Zip

src :: [FilePath]
src = ["src" </> "main", "src" </> "package-info"]

deps :: [FilePath]
deps = ["src" </> "deps-test"]

test :: [FilePath]
test = ["src" </> "test"]

build :: FilePath
build = "build"

javaco :: FilePath
javaco = build </> "classes" </> "javac"

depso :: FilePath
depso = build </> "classes" </> "deps"

testo :: FilePath
testo = build </> "classes" </> "test"

javadoco :: FilePath
javadoco = build </> "javadoc"

scaladoco :: FilePath
scaladoco = build </> "scaladoc"

jardir :: FilePath
jardir = build </> "jar"

releasedir :: FilePath
releasedir = build </> "release"

mavendir :: FilePath
mavendir = build </> "maven"

etcdir :: FilePath
etcdir = "etc"

resources :: FilePath
resources = "resources"

cp :: String
cp = "classpath" ~: [javaco, depso, testo, resources]

wt :: Version -> Maybe String
wt v = Just ("Functional Java " ++ v)

dt :: Version -> Maybe String
dt v = Just ("Functional Java " ++ v ++ " API Specification")

hd :: Maybe String
hd = Just "<div><p><em>Copyright 2008 - 2010 Tony Morris, Runar Bjarnason, Tom Adams, Brad Clow, Ricky Clarkson, Nick Partridge, Jason Zaugg</em></p>This software is released under an open source BSD licence.</div>"

ds :: String
ds = ".deps"

repo :: String
repo = "https://functionaljava.googlecode.com/svn/"

commitMessage :: String
commitMessage = "\"Automated build\""

resolve :: IO ()
resolve = do e <- doesDirectoryExist ds
             unless e $ do mkdir ds
                           mapM_ (\d -> system ("wget -c --directory " ++ ds ++ ' ' : d)) k
  where
  k = map ("http://projects.tmorris.net/public/standards/artifacts/1.30/" ++) ["javadoc-style/javadoc-style.css", "scaladoc-style/script.js", "scaladoc-style/style.css"] ++ ["http://soft.tmorris.net/artifacts/package-list-j2se/1.5.0/package-list"]

type Version = String

readVersion :: IO Version
readVersion = readFile "version"

clean :: IO ()
clean = rmdir build

fullClean :: IO ()
fullClean = rmdir ds >> clean

javac' :: FilePath -> J.Javac
javac' d = J.javac {
  J.directory = Just d,
  J.deprecation = True,
  J.etc = Just "-Xlint:unchecked",
  J.source = Just "1.5",
  J.target = Just "1.5"
}

j :: J.Javac
j = javac' javaco

javac :: IO ExitCode
javac = j ->- src

repl :: IO ExitCode
repl = javac >-- system ("scala -i initrepl " ++ cp)

javadoc' :: Version -> D.Javadoc
javadoc' v = D.javadoc {
  D.directory = Just javadoco,
  D.windowtitle = wt v,
  D.doctitle = dt v,
  D.header = hd,
  D.stylesheetfile = Just (ds </> "javadoc-style.css"),
  D.linkoffline = [("http://java.sun.com/j2se/1.5.0/docs/api", ds)],
  D.linksource = True
}

javadoc :: Version -> IO ExitCode
javadoc v = resolve >> javadoc' v ->- src

nosvn :: FilePather Bool
nosvn = fileName /=? ".svn"

nosvnf :: FilterPredicate
nosvnf = constant nosvn ?&&? isFile

archive :: IO ()
archive = do javac
             mkdir jardir
             writeArchive ([javaco, resources] `zip` repeat ".")
                         nosvn
                         nosvnf
                         [OptVerbose]
                         (jardir </> "functionaljava.jar")

buildAll :: IO ExitCode
buildAll = do v <- readVersion
              resolve
              archive
              javadoc v

maven :: IO ()
maven = do buildAll
           mkdir mavendir
           v <- readVersion
           forM_ [("javadoc", [javadoco]), ("sources", src), ("tests", test)] (\(n, f) ->
             writeHashArchive (map (flip (,) ".") f) nosvn nosvnf [OptVerbose] (mavendir </> "fj-" ++ v ++ '-' :  n ++ ".jar"))

svn :: String -> IO ExitCode
svn k = print k >> system ("svn " ++ k)

svn' :: [String] -> IO ExitCode
svn' = svn . join

release :: IO ExitCode
release = let k = build </> "functionaljava"
              updateVersion z = let (a, b) = second (show . (+1) . read . drop 1) (break (== '.') z)
                                in a ++ '.' : b
          in do buildAll
                mkdir k
                forM_ ([(1, javadoco), (2, jardir), (1, etcdir)] ++ map ((,) 0) src) (\(l, d) -> copyl nosvn nosvnf l d k)
                mkdir releasedir
                writeHashArchive [(build, "functionaljava")] always always' [OptVerbose] (releasedir </> "functionaljava.zip")
                v <- readVersion
                svn' ["import ", build </> "functionaljava", " ", repo, "/artifacts/", v, " -m ", commitMessage]
                svn' ["import ", releasedir, " ", repo, "/artifacts/", v, "/release", " -m ", commitMessage]
                svn' ["copy ", repo, "trunk", " ", repo, "/tags/", v, " -m ", commitMessage]
                length v `seq` writeFile "version" (updateVersion v)
                svn ("commit version -m " ++ commitMessage)
