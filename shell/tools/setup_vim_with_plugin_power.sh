#! /bin/sh
#
# refer http://chrisstrelioff.ws/sandbox/2014/05/29/install_and_setup_vim_on_ubuntu_14_04.html
# to install vim on ubuntu14.04 with effective plugins
#
# set -xe 
set -o errexit

usage() {
    echo "Usage: $0 [Author] [Contact] [ProxyHost:Port]"
    echo "Please care the proxy in your network"
}
if [ "-h" = "$1" ] ; then 
{
    usage
    exit 1 
}
fi

if [ $# -lt 2 ] ; then {
    echo "Error: missing required parametes, get more help info with -h option"
    exit 1
} else {
    AUTHOR=$1
    EMAIL=$2
    if [ ! -z $3 ] ; then {
	proxy=$3    
	export http_proxy="http://$proxy"
	export https_proxy="https://$proxy"
    }
    fi
}
fi
sh_c="sudo -E sh -c "
echo "Starting to intall vim for your favarite editor"
$sh_c 'sleep 3; apt-get update; sleep 3; apt-get -y install vim'

echo "Pathogen is the bundle manager for vim"
mkdir -p $HOME/.vim/autoload  $HOME/.vim/bundle
which curl || $sh_c 'sleep 3; apt-get -y install curl' || echo "fail to install curl" 

curl -LSso $HOME/.vim/autoload/pathogen.vim https://tpo.pe/pathogen.vim

if ! [ -f $HOME/.vimrc ] ; then 
    echo "new vimrc in $HOME"
    touch $HOME/.vimrc
fi

echo "execute pathogen#infect()" >> $HOME/.vimrc 
echo "syntax on" >> $HOME/.vimrc 
echo "filetype plugin indent on" >> $HOME/.vimrc 

echo "#######################################################"
echo "NERDTree is the bundle for file browser with bookmarks"
echo "#######################################################"
git clone  https://github.com/scrooloose/nerdtree.git  $HOME/.vim/bundle/nerdtree

echo "#######################################################"
echo "Tagbar is the bundle of showing code uotlines"
echo "#######################################################"
$sh_c 'sleep 3; apt-get -y install exuberant-ctags'
git clone https://github.com/majutsushi/tagbar $HOME/.vim/bundle/tagbar

which pip || $sh_c 'sleep 3; apt-get -y install python-pip' || echo "fail to install python-pip"

echo "#######################################################"
echo "Jedi-vim is the bundle for auto-complete of python code "
echo "#######################################################"
$sh_c "sleep 3; pip --proxy=$http_proxy install jedi"
git clone https://github.com/davidhalter/jedi-vim.git $HOME/.vim/bundle/jedi-vim

echo "#######################################################"
echo "vim-template is teh bundle as well"
echo "#######################################################"
git clone https://github.com/aperezdc/vim-template.git $HOME/.vim/bundle/vim-template
cat <<EOT >> $HOME/.vimrc  
let g:email= "$EMAIL"
let g:user = "$AUTHOR"
let g:license = "MIT"
EOT
#
# According to http://www.lucianofiandesio.com/vim-configuration-for-happy-java-coding
# add some plugins for java code
#
echo "#######################################################"
echo "install snippet for vim if have python support"
echo "#######################################################"
if $(vim --version|grep -q +python) ; then 
	git clone https://github.com/SirVer/ultisnips.git $HOME/.vim/bundle/ultisnips
	echo "ultisnips are installed with vim"
else
	echo "ultisnips does NOT install for without python support in vim"
fi

echo "#######################################################"
echo "IndentLine is the bundle for highlight indent"
echo "#######################################################"
git clone https://github.com/Yggdroot/indentLine.git  $HOME/.vim/bundle/indentLine

echo "#######################################################"
echo "Supertab is the bundle of auto completition"
echo "#######################################################"
git clone https://github.com/ervandew/supertab.git $HOME/.vim/bundle/supertab

echo "#######################################################"
echo "Delimitmate is the bundle to close of quotes, parenthesis, brackets, etc"
echo "#######################################################"
git clone https://github.com/Raimondi/delimitMate.git $HOME/.vim/bundle/delimitMate

echo "#######################################################"
echo "Go(golang) support for vim"
echo "#######################################################"
git clone https://github.com/fatih/vim-go.git $HOME/.vim/bundle/vim-go
cat <<EOT >> $HOME/.vimrc
let g:go_highlight_functions = 1
let g:go_highlight_methods = 1
let g:go_highlight_structs = 1
let g:go_highlight_operators = 1
let g:go_highlight_build_constraints = 1
let g:go_fmt_command = "goimports"
let g:go_fmt_fail_silently = 1
let g:go_fmt_autosave = 0
let g:go_play_open_browser = 0
EOT

echo "#######################################################"
echo "it is great to install vim with his bundle!"
echo "try with 'vim test.py', 'vim test.sh' and 'test.java'"
echo "#######################################################"
